package uk.gov.hmcts.darts.cache;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.ServiceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class AuthenticationCacheService {
    public static final String CACHE_PREFIX = "darts-gateway-token-service";
    private final DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenGenerator generator;
    private final Duration durationToExpire;
    private final JAXBContext jaxbContext = JAXBContext.newInstance(ServiceContext.class);


    public AuthenticationCacheService(RedisTemplate<String, String> redisTemplate,
                                      TokenGenerator generator,
                                      SecurityProperties securityProperties,
                                      CacheProperties cacheProperties) throws MalformedURLException, JAXBException {
        this.redisTemplate = redisTemplate;
        this.generator = generator;
        this.durationToExpire = Duration.of(cacheProperties.getEntryTimeToIdleSeconds(), ChronoUnit.SECONDS);

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
            JWSAlgorithm.RS256,
            securityProperties.getJwkSource()
        );

        jwtProcessor.setJWSKeySelector(keySelector);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer(securityProperties.getIssuerUri())
            .build();
        var claimsVerifier = new DefaultJWTClaimsVerifier<>(
            securityProperties.getClientId(),
            jwtClaimsSet,
            getClaimSet()
        );


        jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);
    }

    private Set<String> getClaimSet() {
        return new HashSet<>(Arrays.asList(
            JWTClaimNames.AUDIENCE,
            JWTClaimNames.ISSUER,
            JWTClaimNames.ISSUED_AT,
            JWTClaimNames.SUBJECT,
            JWTClaimNames.EXPIRATION_TIME));
    }


    public String getOrCreateValidToken(ServiceContext context) {
        BasicIdentity basicIdentity = getBasicIdentity(context);
        try {
            return getOrCreateValidToken(basicIdentity.getUserName(), basicIdentity.getPassword());
        } catch (Exception e) {
            log.error("error obtaining token", e);
            throw new AuthenticationFailedException("Could not get an identity", e);
        }
    }


    public String getOrCreateValidToken(String username, String password) {
        return getOrCreateValidToken(username, password, false);
    }

    public String getOrCreateValidToken(String username, String password, boolean isRetry) {
        String redisKey = createCacheRedisKey(username, password);
        try {
            String token = redisTemplate.opsForValue().get(redisKey);
            if (token == null) {
                token = createToken(username, password);
                redisTemplate.opsForValue().set(redisKey, token, durationToExpire);
            }
            validateToken(token);
            return token;
        } catch (Exception e) {
            // If token validation fails, delete the token from Redis
            redisTemplate.delete(redisKey);
            if (!isRetry) {
                // Retry once to get a new token
                return getOrCreateValidToken(username, password, true);
            }
            throw e;
        }
    }

    private String createToken(String username, String password) {
        return generator.acquireNewToken(username, password);
    }

    private String createCacheRedisKey(String username, String password) {
        return CACHE_PREFIX + ":TOKEN:" + username + ":" + password;
    }

    private String createServiceContextRedisKey(String token) {
        return CACHE_PREFIX + ":SERVICE-CONTEXT:" + token;
    }

    public void validateToken(String token) {
        try {
            jwtProcessor.process(token, null);
        } catch (ParseException | JOSEException | BadJOSEException e) {
            log.error("JWT Token Validation failed", e);
            throw new AuthenticationFailedException("JWT Token Validation failed", e);
        }
    }

    public void storeTokenContext(String token, ServiceContext context) {
        String redisKey = createServiceContextRedisKey(token);
        try {
            //Set the service context in Redis with an expiration time
            redisTemplate.opsForValue().set(redisKey, serviceContextCacheValue(context), durationToExpire);
        } catch (Exception e) {
            log.error("Error storing service context in Redis", e);
            throw new AuthenticationFailedException("Could not store service context", e);
        }
    }

    public String serviceContextCacheValue(ServiceContext context) {
        try {
            JAXBElement<ServiceContext> serviceContextJaxbElement = new ObjectFactory().createServiceContext(context);
            StringWriter stringWriter = new StringWriter();
            jaxbContext.createMarshaller().marshal(serviceContextJaxbElement, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new AuthenticationFailedException("Failed to marshal ServiceContext", e);
        }
    }

    public ServiceContext getServiceContextFromToken(String token) {
        //Get the cached value and reset its expiration time
        String serviceContext = redisTemplate.opsForValue().getAndExpire(createServiceContextRedisKey(token), durationToExpire);
        if (serviceContext == null) {
            throw new AuthenticationFailedException("Service context not found");
        }
        try {
            StringSource stringSource = new StringSource(serviceContext);
            return jaxbContext.createUnmarshaller().unmarshal(stringSource, ServiceContext.class).getValue();
        } catch (Exception e) {
            throw new AuthenticationFailedException("Failed to unmarshal ServiceContext", e);
        }
    }

    private BasicIdentity getBasicIdentity(ServiceContext context) {
        List<Identity> identities = context.getIdentities();
        if (identities.isEmpty()) {
            throw new AuthenticationFailedException("Could not get an identity in order to fetch a token");
        }

        Identity identity = identities.getFirst();
        if (!(identity instanceof BasicIdentity basicIdentity)) {
            throw new AuthenticationFailedException("Require basic credentials to get a token");
        }
        return basicIdentity;
    }
}

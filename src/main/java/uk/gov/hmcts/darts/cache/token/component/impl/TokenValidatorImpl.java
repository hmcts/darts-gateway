package uk.gov.hmcts.darts.cache.token.component.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenValidationException;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

@Component
@Slf4j
public class TokenValidatorImpl implements TokenValidator {

    private DefaultJWTProcessor<SecurityContext> jwtProcessor;

    private SecurityProperties securityProperties;

    // Lets give our system a chance to refresh the cached token before official token expiry to avoid
    // expiry in transit
    private static final int EXPIRE_JWT_MINUTES_BEFORE_TOKEN_EXPIRY = 5;

    @Autowired
    public TokenValidatorImpl(SecurityProperties securityProperties)  throws MalformedURLException {
        this(securityProperties, new DefaultJWTProcessor<SecurityContext>());
    }

    public TokenValidatorImpl(SecurityProperties securityProperties, DefaultJWTProcessor<SecurityContext> jwtProcessor) throws MalformedURLException {
        this.securityProperties = securityProperties;
        this.jwtProcessor = jwtProcessor;
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
            JWSAlgorithm.RS256,
            securityProperties.getJwkSource()
        );

        jwtProcessor.setJWSKeySelector(keySelector);

        JWTClaimsSet jwtClaimsSet = new Builder()
            .issuer(securityProperties.getIssuerUri())
            .build();
        var claimsVerifier = new DefaultJWTClaimsVerifier<>(
            securityProperties.getClientId(),
            jwtClaimsSet,
            new HashSet<>(Arrays.asList(
                JWTClaimNames.AUDIENCE,
                JWTClaimNames.ISSUER,
                JWTClaimNames.EXPIRATION_TIME,
                JWTClaimNames.ISSUED_AT,
                JWTClaimNames.SUBJECT
            ))
        );

        jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);
    }

    @Override
    public boolean validate(String accessToken) {
        log.debug("Validating JWT: {}", accessToken);
        boolean validated = false;
        try {
            JWT jwt = SignedJWT.parse(accessToken);
            Object expired = jwt.getJWTClaimsSet().getClaim("exp");
            if (expired != null) {
                long expiry = ((Date) expired).getTime() - EXPIRE_JWT_MINUTES_BEFORE_TOKEN_EXPIRY * (1000 * 60);
                long currentTime = Calendar.getInstance().getTime().getTime();
                validated = currentTime < expiry;
            }

            if (validated) {
                jwtProcessor.process(accessToken, null);
            }
            log.debug("JWT Token Validation successful");
        } catch (ParseException | JOSEException | BadJOSEException e) {
            log.error("JWT Token could not be validated", e);
            return false;
        } catch (Exception e) {
            log.error("Major token validation failure", e);
            throw new CacheTokenValidationException("The token validation failed");
        }
        return validated;
    }
}

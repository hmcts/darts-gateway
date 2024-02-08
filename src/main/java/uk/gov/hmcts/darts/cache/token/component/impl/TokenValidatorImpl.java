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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenValidationException;
import uk.gov.hmcts.darts.cache.token.service.Token;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenValidatorImpl implements TokenValidator {

    private DefaultJWTProcessor<SecurityContext> jwtProcessor;

    private final CacheProperties cacheProperties;

    @Autowired
    public TokenValidatorImpl(SecurityProperties securityProperties, CacheProperties properties)  throws MalformedURLException {
        this(securityProperties, new DefaultJWTProcessor<>(), properties);
    }

    public TokenValidatorImpl(SecurityProperties securityProperties,
                              DefaultJWTProcessor<SecurityContext> jwtProcessor, CacheProperties cacheProperties) throws MalformedURLException {
        this.jwtProcessor = jwtProcessor;
        this.cacheProperties = cacheProperties;
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
    public boolean validate(Token.TokenExpiryEnum useExpiryOffset, String accessToken) {
        log.debug("Validating JWT: {}", accessToken);
        boolean validated = false;
        try {
            validated = useExpiryOffset == Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY || validateTheTokenExpiry(accessToken);

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

    boolean validateTheTokenExpiry(String accessToken) {
        boolean validated = false;
        try {
            JWT jwt = SignedJWT.parse(accessToken);
            Object expired = jwt.getJWTClaimsSet().getClaim("exp");
            if (expired != null) {
                long expiryInMillis = ((Date) expired).getTime() - cacheProperties.getSharedTokenEarlyExpirationMinutes() * (1000 * 60);
                long currentTimeMillis = System.currentTimeMillis();
                validated = currentTimeMillis < expiryInMillis;

                if (!validated) {
                    log.info("Detected a token expiry");
                }
            }
        } catch (ParseException e) {
            log.error("JWT Token could not be validated", e);
            validated = false;
        }

        return validated;
    }
}

package uk.gov.hmcts.darts.cache.token.component.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;

@Component
@Slf4j
public class TokenValidatorImpl implements TokenValidator {

    private final DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

    public TokenValidatorImpl(SecurityProperties securityProperties) throws MalformedURLException {
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

        try {
            jwtProcessor.process(accessToken, null);
            log.debug("JWT Token Validation successful");
        } catch (ParseException | JOSEException | BadJOSEException e) {
            log.error("JWT Token could not be validated", e);
            return false;
        }

        return true;
    }
}

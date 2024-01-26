package uk.gov.hmcts.darts.cache.token.component.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenValidationException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

class TokenValidatorImplTest {
    String tokenuri = "token url";
    String scope = "scope";
    String clientid = "clientid";
    String issuerUri = "issuerUri";
    String jwksUri = "http://jwksUri";

    @Test
    void testBasicValidation() throws Exception {
        DefaultJwtProcessorCustomValid custom = new DefaultJwtProcessorCustomValid();
        SecurityProperties securityProperties = Mockito.mock(SecurityProperties.class);

        URL jwksUrl = new URL(jwksUri);
        JWKSource<SecurityContext> source = JWKSourceBuilder.create(jwksUrl).build();

        Mockito.when(securityProperties.getTokenUri()).thenReturn(tokenuri);
        Mockito.when(securityProperties.getClientId()).thenReturn(clientid);
        Mockito.when(securityProperties.getScope()).thenReturn(scope);
        Mockito.when(securityProperties.getIssuerUri()).thenReturn(issuerUri);
        Mockito.when(securityProperties.getJwkSetUri()).thenReturn(jwksUri);
        Mockito.when(securityProperties.getJwkSource()).thenReturn(source);

        TokenValidatorImpl validator = new TokenValidatorImplCustom(securityProperties, custom);

        String token = "token to validate";
        Assertions.assertTrue(validator.validate(token));
    }

    @Test
    void testBasicValidationFalse() throws Exception {
        DefaultJwtProcessorCustom custom = new DefaultJwtProcessorCustom();
        SecurityProperties securityProperties = Mockito.mock(SecurityProperties.class);

        URL jwksUrl = new URL(jwksUri);
        JWKSource<SecurityContext> source = JWKSourceBuilder.create(jwksUrl).build();

        Mockito.when(securityProperties.getTokenUri()).thenReturn(tokenuri);
        Mockito.when(securityProperties.getClientId()).thenReturn(clientid);
        Mockito.when(securityProperties.getScope()).thenReturn(scope);
        Mockito.when(securityProperties.getIssuerUri()).thenReturn(issuerUri);
        Mockito.when(securityProperties.getJwkSetUri()).thenReturn(jwksUri);
        Mockito.when(securityProperties.getJwkSource()).thenReturn(source);

        TokenValidatorImpl validator = new TokenValidatorImplCustom(securityProperties, custom);

        String token = "token to validate";
        Assertions.assertFalse(validator.validate(token));
    }

    @Test
    void testBasicValidationFailure() throws Exception {
        DefaultJwtProcessorThrowable custom = new DefaultJwtProcessorThrowable();
        SecurityProperties securityProperties = Mockito.mock(SecurityProperties.class);

        URL jwksUrl = new URL(jwksUri);
        JWKSource<SecurityContext> source = JWKSourceBuilder.create(jwksUrl).build();

        Mockito.when(securityProperties.getTokenUri()).thenReturn(tokenuri);
        Mockito.when(securityProperties.getClientId()).thenReturn(clientid);
        Mockito.when(securityProperties.getScope()).thenReturn(scope);
        Mockito.when(securityProperties.getIssuerUri()).thenReturn(issuerUri);
        Mockito.when(securityProperties.getJwkSetUri()).thenReturn(jwksUri);
        Mockito.when(securityProperties.getJwkSource()).thenReturn(source);

        TokenValidatorImpl validator = new TokenValidatorImplCustom(securityProperties, custom);

        String token = "token to validate";
        Assertions.assertThrows(CacheTokenValidationException.class, () -> validator.validate(token));
    }

    @Test
    void testValidateTokenExpiry() throws Exception {
        DefaultJwtProcessorThrowable custom = new DefaultJwtProcessorThrowable();
        SecurityProperties securityProperties = Mockito.mock(SecurityProperties.class);

        URL jwksUrl = new URL(jwksUri);
        JWKSource<SecurityContext> source = JWKSourceBuilder.create(jwksUrl).build();

        Mockito.when(securityProperties.getTokenUri()).thenReturn(tokenuri);
        Mockito.when(securityProperties.getClientId()).thenReturn(clientid);
        Mockito.when(securityProperties.getScope()).thenReturn(scope);
        Mockito.when(securityProperties.getIssuerUri()).thenReturn(issuerUri);
        Mockito.when(securityProperties.getJwkSetUri()).thenReturn(jwksUri);
        Mockito.when(securityProperties.getJwkSource()).thenReturn(source);

        TokenValidatorImpl validator = new TokenValidatorImpl(securityProperties, custom);

        Assertions.assertFalse(validator.validateTheTokenExpiry("""
                eyJhbGciOiJSUzI1NiIsImtpZCI6Ilg1ZVhrNHh5b2pORnVtMWtsMll0djhkbE5QNC1jNTdkTzZRR1RWQndhTmsiLCJ0eXAiOiJKV1
                QifQ.eyJpZHAiOiJMb2NhbEFjY291bnQiLCJvaWQiOiJkMDQwZTQxMy1mMWFjLTQ5MDItYjM0Ny0zNjlmNmU1ODI1NTkiLCJzdW
                IiOiJkMDQwZTQxMy1mMWFjLTQ5MDItYjM0Ny0zNjlmNmU1ODI1NTkiLCJnaXZlbl9uYW1lIjoiWGhpYml0IiwiZmFtaWx5X25hbW
                UiOiJVc2VyIiwidGZwIjoiQjJDXzFfcm9wY19kYXJ0c19zaWduaW4iLCJzY3AiOiJGdW5jdGlvbmFsLlRlc3QiLCJhenAiOiI4NT
                ExOGVlMi02NTQ0LTQxODAtYTE2ZS05OTYxNTM2NWYyMDkiLCJ2ZXIiOiIxLjAiLCJpYXQiOjE3MDYyNzA5NzAsImF1ZCI6Ijg1MT
                E4ZWUyLTY1NDQtNDE4MC1hMTZlLTk5NjE1MzY1ZjIwOSIsImV4cCI6MTcwNjI3NDU3MCwiaXNzIjoiaHR0cHM6Ly9obWN0c3N0Z2V
                4dGlkLmIyY2xvZ2luLmNvbS84YjE4NWY4Yi02NjVkLTRiYjMtYWY0YS1hYjdlZTYxYjkzMzQvdjIuMC8iLCJuYmYiOjE3MDYyNzA5
                NzB9.e5AqXvJcq8PfPgz9w-WS_iaDREZdirlPSxZQHPaEgcx08MSPomblUPr_cWhWxHPGOlzrmVqF0bxUe1H0G_3Fn0cAqnq9YnnJ
                klxhd9KzgPp4tRo9TvB2ywklOSHbc4sky9IB-Z48qwO3e1Lm2vGMKSrO114L1ozZe0Ua-36zhFWcpD2HTAQrDDZY_kGMK1tGJ4T8o
                G6zkOgt2UdvxRUN1jFO-M0_V6t0OO9FNCSpBaoYQDOcu8KvRdON9Www-a2yEtYkkVTY0TkVYVCYu4u00U0A1QGAzJ0P73iiSqvcL
                F8vnj6XPHLGAZA0yg8Dzxx5qf09WLoDbD0hSBqBDlTojg:
                """));
    }

    class DefaultJwtProcessorCustom extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(String jwtString, SecurityContext context) throws ParseException, BadJOSEException, JOSEException {
            return super.process(jwtString, context);
        }

        boolean validateTheTokenExpiry(String accessToken) {
            // do not worry about validating the token for expiry directly
            return true;
        }
    }

    class DefaultJwtProcessorCustomValid extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(String jwtString, SecurityContext context) throws ParseException, BadJOSEException, JOSEException {
            return null;
        }

        boolean validateTheTokenExpiry(String accessToken) {
            // do not worry about validating the token for expiry directly
            return true;
        }
    }

    class DefaultJwtProcessorThrowable extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(String jwtString, SecurityContext context) throws ParseException, BadJOSEException, JOSEException {
            throw new UnsupportedOperationException("", null);
        }
    }

    class TokenValidatorImplCustom extends TokenValidatorImpl {

        public TokenValidatorImplCustom(SecurityProperties securityProperties) throws MalformedURLException {
            super(securityProperties);
        }

        public TokenValidatorImplCustom(SecurityProperties securityProperties, DefaultJWTProcessor<SecurityContext> jwtProcessor) throws MalformedURLException {
            super(securityProperties, jwtProcessor);
        }

        @Override
        boolean validateTheTokenExpiry(String accessToken) {
            // do not worry about validating the token for expiry directly
            return true;
        }
    }
}

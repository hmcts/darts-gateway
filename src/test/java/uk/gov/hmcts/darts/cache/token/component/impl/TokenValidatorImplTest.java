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

        TokenValidatorImpl validator = new TokenValidatorImpl(securityProperties, custom);

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

        TokenValidatorImpl validator = new TokenValidatorImpl(securityProperties, custom);

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

        TokenValidatorImpl validator = new TokenValidatorImpl(securityProperties, custom);

        String token = "token to validate";
        Assertions.assertThrows(CacheTokenValidationException.class, () -> validator.validate(token));
    }

    class DefaultJwtProcessorCustom extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(String jwtString, SecurityContext context) throws ParseException, BadJOSEException, JOSEException {
            return super.process(jwtString, context);
        }
    }

    class DefaultJwtProcessorCustomValid extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(String jwtString, SecurityContext context) throws ParseException, BadJOSEException, JOSEException {
            return null;
        }
    }

    class DefaultJwtProcessorThrowable extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(String jwtString, SecurityContext context) throws ParseException, BadJOSEException, JOSEException {
            throw new UnsupportedOperationException("", null);
        }
    }
}

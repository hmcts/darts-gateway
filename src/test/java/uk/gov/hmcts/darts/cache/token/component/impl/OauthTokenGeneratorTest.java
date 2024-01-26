package uk.gov.hmcts.darts.cache.token.component.impl;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;

import java.net.URI;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class OauthTokenGeneratorTest {

    @Mock
    private SecurityProperties securityProperties;

    static final String TOKEN_URI = "http://tokenurl";
    static final String SCOPE = "scope";
    static final String CLIENT_ID = "clientid";

    @BeforeEach
    public void befoerTest() {
        Mockito.when(securityProperties.getTokenUri()).thenReturn(TOKEN_URI);
        Mockito.when(securityProperties.getClientId()).thenReturn(CLIENT_ID);
        Mockito.when(securityProperties.getScope()).thenReturn(SCOPE);
    }

    @Test
    void testAcquireTokenValid() {
        String tokenValue = """
            {"access_token":"token"}
            """;

        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, tokenValue);

        String username = "username";
        String password = "password";

        Assertions.assertEquals("token", generatorCustom.acquireNewToken(username, password));
        Assertions.assertEquals(5, generatorCustom.parameters.size());
        Assertions.assertEquals(TOKEN_URI, generatorCustom.uriSpecified.toString());
        Assertions.assertEquals("password", generatorCustom.parameters.get(OauthTokenGenerator.GRANT_TYPE_PARAMETER_KEY));
        Assertions.assertEquals(password, generatorCustom.parameters.get(OauthTokenGenerator.PASSWORD_PARAMETER_KEY));
        Assertions.assertEquals(username, generatorCustom.parameters.get(OauthTokenGenerator.USERNAME_PARAMETER_KEY));
        Assertions.assertEquals(SCOPE, generatorCustom.parameters.get(OauthTokenGenerator.SCOPE_PARAMETER_KEY));
        Assertions.assertEquals(CLIENT_ID, generatorCustom.parameters.get(OauthTokenGenerator.CLIENT_ID_PARAMETER_KEY));

    }

    @Test
    void testAcquireTokenInvalidToken() {
        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, "");

        String username = "username";
        String password = "password";

        Assertions.assertThrows(CacheTokenCreationException.class, () -> generatorCustom.acquireNewToken(username, password));
        Assertions.assertEquals(5, generatorCustom.parameters.size());
        Assertions.assertEquals(TOKEN_URI, generatorCustom.uriSpecified.toString());
        Assertions.assertEquals("password", generatorCustom.parameters.get(OauthTokenGenerator.GRANT_TYPE_PARAMETER_KEY));
        Assertions.assertEquals(password, generatorCustom.parameters.get(OauthTokenGenerator.PASSWORD_PARAMETER_KEY));
        Assertions.assertEquals(username, generatorCustom.parameters.get(OauthTokenGenerator.USERNAME_PARAMETER_KEY));
        Assertions.assertEquals(SCOPE, generatorCustom.parameters.get(OauthTokenGenerator.SCOPE_PARAMETER_KEY));
        Assertions.assertEquals(CLIENT_ID, generatorCustom.parameters.get(OauthTokenGenerator.CLIENT_ID_PARAMETER_KEY));
    }

    @Setter
    class OauthTokenGeneratorCustom extends OauthTokenGenerator {

        private String response;

        private URI uriSpecified;

        @Getter
        private Map<String, String> parameters;

        public OauthTokenGeneratorCustom(SecurityProperties securityProperties, String token) {
            super(securityProperties);
            this.response = token;
        }

        @Override
        protected String makeCall(URI url, Map<String, String> params) {
            uriSpecified = url;
            parameters = params;
            return response;
        }
    }

}

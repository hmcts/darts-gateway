package uk.gov.hmcts.darts.cache.token.component.impl;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class OauthTokenGeneratorTest {

    @Mock
    private SecurityProperties securityProperties;

    static final String TOKEN_URI = "http://tokenurl";
    static final String SCOPE = "scope";
    static final String CLIENT_ID = "clientid";

    @Test
    void testAcquireTokenValid() {
        Mockito.when(securityProperties.getTokenUri()).thenReturn(TOKEN_URI);
        Mockito.when(securityProperties.getClientId()).thenReturn(CLIENT_ID);
        Mockito.when(securityProperties.getScope()).thenReturn(SCOPE);
        Mockito.when(securityProperties.isUserExternalInternalMappingsEnabled()).thenReturn(true);

        List<ExternalUserToInternalUserMappingImpl> mappingList = new ArrayList<>();
        ExternalUserToInternalUserMappingImpl mapping = new ExternalUserToInternalUserMappingImpl();
        mappingList.add(mapping);

        String username = "viqUser";
        mapping.setUserName(username);

        String externalViqPassword = "viqExternalPassword";
        mapping.setExternalPassword(externalViqPassword);

        String internalViqPassword = "viqInternalPassword";
        mapping.setInternalPassword(internalViqPassword);


        Mockito.when(securityProperties.getUserExternalInternalMappings()).thenReturn(mappingList);

        String tokenValue = """
            {"access_token":"token"}
            """;

        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, tokenValue);

        Assertions.assertEquals("token", generatorCustom.acquireNewToken(username, externalViqPassword));
        Assertions.assertEquals(5, generatorCustom.parameters.size());
        Assertions.assertEquals(TOKEN_URI, generatorCustom.uriSpecified.toString());
        Assertions.assertEquals("password", generatorCustom.parameters.get(OauthTokenGenerator.GRANT_TYPE_PARAMETER_KEY));
        Assertions.assertEquals(internalViqPassword, generatorCustom.parameters.get(OauthTokenGenerator.PASSWORD_PARAMETER_KEY));
        Assertions.assertEquals(username, generatorCustom.parameters.get(OauthTokenGenerator.USERNAME_PARAMETER_KEY));
        Assertions.assertEquals(SCOPE, generatorCustom.parameters.get(OauthTokenGenerator.SCOPE_PARAMETER_KEY));
        Assertions.assertEquals(CLIENT_ID, generatorCustom.parameters.get(OauthTokenGenerator.CLIENT_ID_PARAMETER_KEY));
    }

    @Test
    void testAcquireTokenInvalidUserAccordingToMapping() {
        Mockito.when(securityProperties.isUserExternalInternalMappingsEnabled()).thenReturn(true);

        String username = "viqUser";

        List<ExternalUserToInternalUserMappingImpl> mappingList = new ArrayList<>();
        ExternalUserToInternalUserMappingImpl mapping = new ExternalUserToInternalUserMappingImpl();
        mapping.setUserName(username);
        mappingList.add(mapping);
        Mockito.when(securityProperties.getUserExternalInternalMappings()).thenReturn(mappingList);

        String tokenValue = """
            {"access_token":"token"}
            """;

        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, tokenValue);

        String invalidUserName = "invalidviqUser";
        String externalViqPassword = "viqExternalPassword";
        Assertions.assertThrows(CacheTokenCreationException.class, () -> generatorCustom.acquireNewToken(invalidUserName, externalViqPassword));
    }

    @Test
    void testAcquireTokenInvalidPasswordAccordingToMapping() {
        Mockito.when(securityProperties.isUserExternalInternalMappingsEnabled()).thenReturn(true);

        String username = "viqUser";
        String externalViqPassword = "viqExternalPassword";

        List<ExternalUserToInternalUserMappingImpl> mappingList = new ArrayList<>();
        ExternalUserToInternalUserMappingImpl mapping = new ExternalUserToInternalUserMappingImpl();
        mapping.setUserName(username);
        mapping.setExternalPassword(externalViqPassword);

        mappingList.add(mapping);
        Mockito.when(securityProperties.getUserExternalInternalMappings()).thenReturn(mappingList);

        String tokenValue = """
            {"access_token":"token"}
            """;

        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, tokenValue);

        String invalidExternalViqPassword = "viqExternalPassword";
        Assertions.assertThrows(CacheTokenCreationException.class, () -> generatorCustom.acquireNewToken(username, invalidExternalViqPassword));
    }

    @Test
    void testAcquireTokenInvalidToken() {
        Mockito.when(securityProperties.getTokenUri()).thenReturn(TOKEN_URI);
        Mockito.when(securityProperties.getClientId()).thenReturn(CLIENT_ID);
        Mockito.when(securityProperties.getScope()).thenReturn(SCOPE);

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

    @Test
    void testAcquireTokenEncode() {
        Map<String, String> mapParamToEncode = new HashMap<>();

        String key1 = "testkey";
        String value1 = "testvalue";
        String key2 = "testkey1";
        String value2 = "testvalue1";

        mapParamToEncode.put("testkey", "testvalue");
        mapParamToEncode.put("testkey1", "testvalue1");

        String paramEncode = OauthTokenGeneratorCustom.encode(mapParamToEncode);

        Assertions.assertEquals(key1 + "=" + value1 + "&" + key2 + "=" + value2, paramEncode);
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

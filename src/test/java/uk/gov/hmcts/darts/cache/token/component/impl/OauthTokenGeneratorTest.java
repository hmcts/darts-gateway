package uk.gov.hmcts.darts.cache.token.component.impl;

import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;

import java.util.HashMap;
import java.util.Map;

class OauthTokenGeneratorTest {
    @Test
    void testAcquireTokenValid() {
        SecurityProperties securityProperties = Mockito.mock(SecurityProperties.class);
        String tokenKey = "access_token";
        String tokenValue = "actual token";

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(tokenKey, tokenValue);

        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, new RestTemplate(), responseMap);
        generatorCustom.response = responseMap;

        String tokenuri = "token url";
        String scope = "scope";
        String clientid = "clientid";

        Mockito.when(securityProperties.getTokenUri()).thenReturn(tokenuri);
        Mockito.when(securityProperties.getClientId()).thenReturn(clientid);
        Mockito.when(securityProperties.getScope()).thenReturn(scope);

        String username = "username";
        String password = "password";

        String token = generatorCustom.acquireNewToken(username, password);
        Assertions.assertEquals(clientid, generatorCustom.getParameters().get("client_id").get(0));
        Assertions.assertEquals(scope, generatorCustom.getParameters().get("scope").get(0));
        Assertions.assertEquals(username, generatorCustom.getParameters().get("username").get(0));
        Assertions.assertEquals(password, generatorCustom.getParameters().get("password").get(0));
        Assertions.assertEquals(token, tokenValue);
    }

    @Test
    void testAcquireTokenInvalid() {
        SecurityProperties securityProperties = Mockito.mock(SecurityProperties.class);
        String tokenKey = "access_token invalid";
        String tokenValue = "actual token";

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(tokenKey, tokenValue);

        OauthTokenGeneratorCustom generatorCustom = new OauthTokenGeneratorCustom(securityProperties, new RestTemplate(), responseMap);
        generatorCustom.response = responseMap;

        String tokenuri = "token url";
        String scope = "scope";
        String clientid = "clientid";

        Mockito.when(securityProperties.getTokenUri()).thenReturn(tokenuri);
        Mockito.when(securityProperties.getClientId()).thenReturn(clientid);
        Mockito.when(securityProperties.getScope()).thenReturn(scope);

        String username = "username";
        String password = "password";

        Assertions.assertThrows(CacheTokenCreationException.class, () -> generatorCustom.acquireNewToken(username, password));
    }

    class OauthTokenGeneratorCustom extends OauthTokenGenerator {

        private Map<?, ?> response;

        @Getter
        private MultiValueMap<String, String> parameters;

        public OauthTokenGeneratorCustom(SecurityProperties securityProperties, RestTemplate template, Map<?, ?> response) {
            super(securityProperties, template);
            this.response = response;
        }

        @Override
        Map<?, ?> acquireToken(HttpEntity<MultiValueMap<String, String>> requestValues, String username, String password) {
            parameters = requestValues.getBody();
            return response;
        }
    }

}

package uk.gov.hmcts.darts.cache.token.component.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OauthTokenGenerator implements TokenGenerator {
    private final SecurityProperties securityProperties;

    private final RestTemplate template;

    public String acquireNewToken(String username, String password) {
        HttpEntity<MultiValueMap<String, String>> values = buildTokenRequestEntity(username, password);

        Map tokenMap = acquireToken(values, username, password);

        if (tokenMap != null && tokenMap.containsKey("access_token")) {
            return (String) tokenMap.get("access_token");
        }

        throw new CacheTokenCreationException("Token details not found");
    }

    Map<?,?> acquireToken(HttpEntity<MultiValueMap<String, String>> requestValues, String username, String password) {
        Map<?,?> tokenMap = template.exchange(securityProperties.getTokenUri(), HttpMethod.POST, buildTokenRequestEntity(username, password), Map.class)
            .getBody();
        return tokenMap;
    }

    private HttpEntity<MultiValueMap<String, String>> buildTokenRequestEntity(String username, String password) {
        MultiValueMap<String, String> formValues = new LinkedMultiValueMap<>();
        formValues.add("grant_type", "password");
        formValues.add("client_id", securityProperties.getClientId());
        formValues.add("scope", securityProperties.getScope());

        if (username != null) {
            formValues.add("username", username);
        }

        if (password != null) {
            formValues.add("password", password);
        }

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(formValues, headers);
    }
}

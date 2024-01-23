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

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OauthTokenGenerator implements TokenGenerator {
    private final SecurityProperties securityProperties;

    private final RestTemplate template;

    public String acquireNewToken(String username, String password) {
        return template.exchange(securityProperties.getTokenUri(), HttpMethod.POST, buildTokenRequestEntity(username, password), Map.class)
            .getBody().get("access_token").toString();
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

package uk.gov.hmcts.darts.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class OauthTokenGenerator {
    @Value("${azure-ad-ropc.token-uri}")
    private String tokenUri;
    @Value("${azure-ad-ropc.scope}")
    private String scope;
    @Value("${azure-ad-ropc.username}")
    private String username;
    @Value("${azure-ad-ropc.password}")
    private String password;
    @Value("${azure-ad-ropc.client-id}")
    private String clientId;

    private final RestTemplate template;

    public String acquireNewToken() {
        return template.exchange(tokenUri, HttpMethod.POST, buildTokenRequestEntity(), Map.class)
            .getBody().get("access_token").toString();
    }

    public String acquireNewToken(String username, String password) {
        return template.exchange(tokenUri, HttpMethod.POST, buildTokenRequestEntity(username, password), Map.class)
            .getBody().get("access_token").toString();
    }

    private HttpEntity<MultiValueMap<String, String>> buildTokenRequestEntity() {
        return buildTokenRequestEntity(username, password);
    }

    private HttpEntity<MultiValueMap<String, String>> buildTokenRequestEntity(String username, String password) {
        MultiValueMap<String, String> formValues = new LinkedMultiValueMap<>();
        formValues.add("grant_type", "password");
        formValues.add("client_id", clientId);
        formValues.add("scope", scope);

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

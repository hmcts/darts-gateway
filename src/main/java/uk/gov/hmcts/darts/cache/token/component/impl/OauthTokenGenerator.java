package uk.gov.hmcts.darts.cache.token.component.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OauthTokenGenerator implements TokenGenerator {
    private final SecurityProperties securityProperties;

    public static final String CLIENT_ID_PARAMETER_KEY = "client_id";
    public static final String SCOPE_PARAMETER_KEY = "scope";
    public static final String GRANT_TYPE_PARAMETER_KEY = "grant_type";
    public static final String USERNAME_PARAMETER_KEY = "username";
    public static final String PASSWORD_PARAMETER_KEY = "password";


    @SneakyThrows
    public String acquireNewToken(String username, String password) {
        Map<String, String> params = Map.of(CLIENT_ID_PARAMETER_KEY, securityProperties.getClientId(),
                SCOPE_PARAMETER_KEY, securityProperties.getScope(),
                GRANT_TYPE_PARAMETER_KEY, "password",
                USERNAME_PARAMETER_KEY, username,
                PASSWORD_PARAMETER_KEY, password
        );

        String response = makeCall(URI.create(securityProperties.getTokenUri()), params);

        TokenResponse tokenResponse;
        if (response != null && !response.isEmpty()) {
            tokenResponse = new ObjectMapper()
                    .readValue(response, TokenResponse.class);

            if (tokenResponse.accessToken() == null) {
                throw new CacheTokenCreationException("No token found for user name and password");
            }
        } else {
            throw new CacheTokenCreationException("No token found for user name and password");
        }

        return tokenResponse.accessToken();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private HttpRequest.BodyPublisher encode(Map<String, String> params) {
        String urlEncoded = params.entrySet()
                .stream()
                .map(entry -> new StringJoiner("=")
                        .add(entry.getKey())
                        .add(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .toString())
                .collect(Collectors.joining("&"));

        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {}

    @SneakyThrows
    protected String makeCall(URI url, Map<String, String> params) {
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(encode(params))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();
    }
}

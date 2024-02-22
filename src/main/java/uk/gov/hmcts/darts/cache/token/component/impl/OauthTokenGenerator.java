package uk.gov.hmcts.darts.cache.token.component.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.config.ExternalUserToInternalUserMapping;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
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

        // if the internal to external mapping is enabled then throw an error if we cant find an internal password
        if (securityProperties.isUserExternalInternalMappingsEnabled()) {
            Optional<String> fndPassword = findInternalUserPassword(username, password);
            if (fndPassword.isEmpty()) {
                throw new CacheTokenCreationException("No token found for user name and password");
            }
            password = fndPassword.get();
        }

        Map<String, String> params = Map.of(CLIENT_ID_PARAMETER_KEY, securityProperties.getClientId(),
                SCOPE_PARAMETER_KEY, securityProperties.getScope(),
                GRANT_TYPE_PARAMETER_KEY, PASSWORD_PARAMETER_KEY,
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

    private Optional<String> findInternalUserPassword(String userName, String externalPassword) {
        Optional<ExternalUserToInternalUserMappingImpl> externalUserToInternalUserMapping
            = securityProperties.getUserExternalInternalMappings()
            .stream().filter(mapping -> mapping.getUserName().equals(userName)
                && mapping.getExternalPassword().equals(externalPassword)).findFirst();

        return externalUserToInternalUserMapping.map(ExternalUserToInternalUserMapping::getInternalPassword);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    public static String encode(Map<String, String> params) {
        String urlEncoded = params.entrySet()
                .stream()
                .map(entry -> new StringJoiner("=")
                        .add(entry.getKey())
                        .add(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .toString())
                .collect(Collectors.joining("&"));

        return urlEncoded;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {}

    @SneakyThrows
    protected String makeCall(URI url, Map<String, String> params) {
        HttpRequest.BodyPublisher parametersToSend = HttpRequest.BodyPublishers.ofString(encode(params));
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(parametersToSend)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();
    }
}

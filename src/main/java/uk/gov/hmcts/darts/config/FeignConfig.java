package uk.gov.hmcts.darts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.utilities.LocalDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.LocalDateTypeAdapter;
import uk.gov.hmcts.darts.utilities.OffsetDateTimeTypeAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Configuration
public class FeignConfig {

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


    @Bean
    public Encoder encoder(Gson gson) {
        return new GsonEncoder(gson);
    }

    @Bean
    public Decoder decoder(Gson gson) {
        return new GsonDecoder(gson);
    }

    @Bean
    public Client client() {
        return new OkHttpClient();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
              .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
              .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
              .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
              .create();
    }

    // Temporary fix for service to service authentication.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Temporary fix for service to service authentication.
    @Bean
    @Profile("!int-test")
    public RequestInterceptor requestInterceptor(RestTemplate restTemplate) {
        return
              template -> {
                  var entity = buildTokenRequestEntity();
                  var tokenResponse = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, Map.class).getBody();
                  template.header("Authorization", "Bearer " + tokenResponse.get("access_token"));
              };
    }

    // Temporary fix for service to service authentication.
    private HttpEntity<MultiValueMap<String, String>> buildTokenRequestEntity() {
        MultiValueMap<String, String> formValues = new LinkedMultiValueMap<>();
        formValues.add("grant_type","password");
        formValues.add("client_id",clientId);
        formValues.add("scope",scope);
        formValues.add("username",username);
        formValues.add("password",password);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(formValues, headers);
    }
}

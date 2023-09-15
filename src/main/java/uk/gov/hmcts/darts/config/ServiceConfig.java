package uk.gov.hmcts.darts.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.OffsetDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.utilities.serializer.LocalDateTimeTypeSerializer;
import uk.gov.hmcts.darts.utilities.serializer.LocalDateTypeSerializer;
import uk.gov.hmcts.darts.utilities.serializer.OffsetDateTimeTypeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * DO NOT ANNOTATE THIS CLASS WITH @Configuration. This class works alongside the
 * {@link org.springframework.cloud.openfeign.FeignClient} annotation. In this way configurations can be specific API
 * feign interaction and not applied globally
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class ServiceConfig {

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
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Client client() {
        return new OkHttpClient();
    }

    @Bean
    @Primary
    public ObjectMapper boundServiceObjectMapper() {
        return getServiceObjectMapper();
    }


    public static ObjectMapper getServiceObjectMapper() {
        JavaTimeModule module = new JavaTimeModule();

        module.addSerializer(LocalDateTime.class, new LocalDateTimeTypeSerializer())
            .addSerializer(LocalDate.class, new LocalDateTypeSerializer())
            .addSerializer(OffsetDateTime.class, new OffsetDateTimeTypeSerializer())
            .addDeserializer(LocalDateTime.class, new LocalDateTimeTypeDeserializer())
            .addDeserializer(LocalDate.class, new LocalDateTypeDeserializer())
            .addDeserializer(OffsetDateTime.class, new OffsetDateTimeTypeDeserializer());

        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module);
    }

    @Bean
    public Decoder feignDecoder() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(getServiceObjectMapper());

        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jacksonConverter);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> httpMessageConverters;
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory, new EmptyObjectProvider<>()));
    }

    @Bean
    public Encoder feignEncoder() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(getServiceObjectMapper());

        HttpMessageConverters httpMessageConverters = new HttpMessageConverters(jacksonConverter);
        ObjectFactory<HttpMessageConverters> objectFactory = () -> httpMessageConverters;

        return new SpringEncoder(objectFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
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
        formValues.add("grant_type", "password");
        formValues.add("client_id", clientId);
        formValues.add("scope", scope);
        formValues.add("username", username);
        formValues.add("password", password);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(formValues, headers);
    }

    class EmptyObjectProvider<T> implements ObjectProvider<T> {

        @Override
        public T getObject(Object... args)  {
            return null;
        }

        @Override
        public T getObject() {
            return null;
        }

        @Override
        public void forEach(Consumer action) {
            // do nothing
        }

        @Override
        public T getIfAvailable() {
            return null;
        }

        @Override
        public T getIfUnique() {
            return null;
        }
    }
}

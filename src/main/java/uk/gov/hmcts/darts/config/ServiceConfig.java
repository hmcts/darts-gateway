package uk.gov.hmcts.darts.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;
import uk.gov.hmcts.darts.common.client.exeption.JacksonDartsClientProblemDecoder;
import uk.gov.hmcts.darts.common.client.exeption.JacksonFeignClientProblemDecoder;
import uk.gov.hmcts.darts.common.client.mapper.APIProblemResponseMapper;
import uk.gov.hmcts.darts.common.client.mapper.CommonApiProblemResponseMapper;
import uk.gov.hmcts.darts.common.client.mapper.DailyListAPIProblemResponseMapper;
import uk.gov.hmcts.darts.common.client.mapper.EventAPIProblemResponseMapper;
import uk.gov.hmcts.darts.metadata.ContextRegistryEndpointMetaData;
import uk.gov.hmcts.darts.metadata.DartsEndpointMetaData;
import uk.gov.hmcts.darts.metadata.EndpointMetaData;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.LocalDateTypeDeserializer;
import uk.gov.hmcts.darts.utilities.deserializer.OffsetDateTimeTypeDeserializer;
import uk.gov.hmcts.darts.utilities.serializer.LocalDateTimeTypeSerializer;
import uk.gov.hmcts.darts.utilities.serializer.LocalDateTypeSerializer;
import uk.gov.hmcts.darts.utilities.serializer.OffsetDateTimeTypeSerializer;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("PMD.ExcessiveImports")
@Configuration
public class ServiceConfig {

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
    public Clock clock() {
        return Clock.systemDefaultZone();
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

    @Bean
    public List<APIProblemResponseMapper> getResponseMappers() {
        return Arrays.asList(
            new CommonApiProblemResponseMapper(),
            new DailyListAPIProblemResponseMapper(),
            new EventAPIProblemResponseMapper()
        );
    }

    @Bean
    public ErrorDecoder feignErrorDecoder(List<APIProblemResponseMapper> mappers) {
        return new JacksonFeignClientProblemDecoder(mappers);
    }

    @Bean
    public DartsClientProblemDecoder dartsDecoder(List<APIProblemResponseMapper> mappers) {
        return new JacksonDartsClientProblemDecoder(mappers);
    }


    @Bean
    public RestTemplate getTemplate() {
        return new RestTemplate();
    }

    @Bean
    List<EndpointMetaData> getEndpointMetaData() {
        List<EndpointMetaData> endpointMetaData = new ArrayList<>();
        endpointMetaData.add(new DartsEndpointMetaData(SoapWebServiceConfig.BASE_WEB_CONTEXT));
        endpointMetaData.add(new ContextRegistryEndpointMetaData(SoapWebServiceConfig.BASE_WEB_CONTEXT));
        return endpointMetaData;
    }
}

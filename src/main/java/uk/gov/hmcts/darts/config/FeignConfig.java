package uk.gov.hmcts.darts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Client;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.darts.utilities.LocalDateTimeTypeAdapter;
import uk.gov.hmcts.darts.utilities.LocalDateTypeAdapter;
import uk.gov.hmcts.darts.utilities.OffsetDateTimeTypeAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Configuration
public class FeignConfig {

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
}

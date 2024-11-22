package uk.gov.hmcts.darts.conf;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;

@TestConfiguration
public class ServiceTestConfiguration {
    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(Instant.now(), Clock.systemUTC().getZone());
    }
}

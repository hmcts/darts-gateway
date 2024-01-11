package uk.gov.hmcts.darts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import uk.gov.hmcts.darts.cache.token.config.KeyConfiguration;

@SpringBootApplication
@EnableFeignClients
@EnableRedisRepositories (keyspaceConfiguration= KeyConfiguration.class)
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

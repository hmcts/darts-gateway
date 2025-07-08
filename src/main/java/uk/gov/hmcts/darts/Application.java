package uk.gov.hmcts.darts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import uk.gov.hmcts.darts.cache.token.config.KeyConfiguration;
import uk.gov.hmcts.darts.shutdown.GracefulShutdownHook;

@SpringBootApplication
@EnableFeignClients
@EnableRedisRepositories(keyspaceConfiguration = KeyConfiguration.class)
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    @SuppressWarnings("PMD.DoNotUseThreads")//Required for shutdown hook)
    public static void main(final String[] args) {
        final var application = new SpringApplication(Application.class);
        application.setRegisterShutdownHook(false);

        ConfigurableApplicationContext applicationContext = application.run(args);

        Thread shutdownHookThread = new Thread(new GracefulShutdownHook((ServletWebServerApplicationContext) applicationContext));
        shutdownHookThread.setName("GracefulShutdownHook");
        Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    }
}

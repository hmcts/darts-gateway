package uk.gov.hmcts.darts.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

/**
 * A configuration that can be used with
 */
@TestConfiguration
public class RedisConfiguration {
    public static RedisServer redisServer;

    public RedisConfiguration() {
        this.redisServer = new RedisServer();
    }

    @PostConstruct
    public void postConstruct() {
        if (!redisServer.isActive()) {
            redisServer.start();
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (redisServer.isActive()) {
            redisServer.stop();
        }
    }
}

package uk.gov.hmcts.darts.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
class RedisConfiguration {
    public static final RedisServer REDISSERVER = new RedisServer();

    @PostConstruct
    public void postConstruct() {
        if (!REDISSERVER.isActive()) {
            REDISSERVER.start();
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (REDISSERVER.isActive()) {
            REDISSERVER.stop();
        }
    }
}

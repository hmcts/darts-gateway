package uk.gov.hmcts.darts.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import uk.gov.hmcts.darts.workflow.command.DeployRedisCommand;

import java.io.IOException;

@TestConfiguration
class RedisConfiguration {
    public static final DeployRedisCommand REDIS_COMMAND = new DeployRedisCommand();

    private int port;


    @PostConstruct
    public void postConstruct() throws IOException {
        REDIS_COMMAND.executeWithDocker();
    }

    public static DeployRedisCommand getDeployRedisCommand() {
        return REDIS_COMMAND;
    }
}

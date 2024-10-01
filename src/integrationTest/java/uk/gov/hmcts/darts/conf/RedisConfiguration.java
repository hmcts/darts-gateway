package uk.gov.hmcts.darts.conf;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import uk.gov.hmcts.darts.workflow.command.DeployRedisCommand;

import java.io.IOException;

@TestConfiguration
public class RedisConfiguration {
    public static final DeployRedisCommand REDIS_COMMAND = new DeployRedisCommand();

    private int port;


    @PostConstruct
    public void postConstruct() throws IOException {
        REDIS_COMMAND.executeWithDocker(REDIS_COMMAND.getArguments());
    }

    public static DeployRedisCommand getDeployRedisCommand() {
        return REDIS_COMMAND;
    }
}
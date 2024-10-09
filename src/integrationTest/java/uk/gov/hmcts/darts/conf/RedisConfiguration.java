package uk.gov.hmcts.darts.conf;

import org.springframework.boot.test.context.TestConfiguration;
import uk.gov.hmcts.darts.workflow.command.DeployRedisCommand;

@TestConfiguration
public class RedisConfiguration {
    public static final DeployRedisCommand REDIS_COMMAND = new DeployRedisCommand();

    private int port;

    public static DeployRedisCommand getDeployRedisCommand() {
        return REDIS_COMMAND;
    }
}
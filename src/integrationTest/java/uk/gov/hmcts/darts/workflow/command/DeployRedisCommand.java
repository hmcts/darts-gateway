package uk.gov.hmcts.darts.workflow.command;

import java.io.IOException;

public class DeployRedisCommand implements Command {

    // private static GenericContainer<?> container;

    /*
    static {
        ImageFromDockerfile redisImage = new ImageFromDockerfile()
            .withDockerfileFromBuilder(builder ->
                                           builder
                                               .from("redis:7.2.4-alpine")
                                               .build());

        container = new GenericContainer<>(redisImage);
        container = container.withExposedPorts(6379);

        container.start();
        System.setProperty("darts-gateway.redis.connection-string", "redis://localhost:" + container.getMappedPort(6379).toString());
        System.setProperty("darts-gateway.redis.ssl-enabled", "false");
    }
*/
    @Override
    public void cleanupResources() {
        //container.stop();
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeWithDocker() throws IOException {

        /*
        if (!container.isRunning()) {
            container.start();
        }

         */
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}

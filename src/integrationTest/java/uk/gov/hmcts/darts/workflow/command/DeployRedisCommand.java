package uk.gov.hmcts.darts.workflow.command;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.IOException;

public class DeployRedisCommand implements Command {

    static {
        ImageFromDockerfile redisImage = new ImageFromDockerfile()
            .withDockerfileFromBuilder(builder ->
                                           builder
                                               .from("hmctspublic.azurecr.io/imported/redis:7.2.4-alpine")
                                               .build());

        GenericContainer<?> container = new GenericContainer<>(redisImage);
        container = container.withExposedPorts(6379);

        container.start();
        System.setProperty("darts-gateway.redis.connection-string", "redis://localhost:" + container.getMappedPort(6379).toString());
        System.setProperty("darts-gateway.redis.ssl-enabled", "false");
    }

    @Override
    public void cleanupResources() {
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeWithDocker() throws IOException {
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}

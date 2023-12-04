package uk.gov.hmcts.darts.workflow.command;

import com.github.dockerjava.api.model.VolumesFrom;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;

public interface Command {
    void cleanupResources();

    void execute() throws Exception;

    void executeWithDocker() throws Exception;

    boolean isSuccess();
}

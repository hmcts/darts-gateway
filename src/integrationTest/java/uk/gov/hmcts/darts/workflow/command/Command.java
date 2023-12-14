package uk.gov.hmcts.darts.workflow.command;

import java.io.IOException;

public interface Command {
    void cleanupResources();

    void execute() throws IOException;

    void executeWithDocker() throws IOException;

    boolean isSuccess();
}

package uk.gov.hmcts.darts.workflow.command;

import java.io.IOException;
import java.util.Map;

public interface Command {
    void cleanupResources();

    void execute(Map<String, String> envVariables) throws IOException;

    void executeWithDocker(Map<String, String> envVariables) throws IOException;

    boolean isSuccess();

    Map<String, String> getArguments();

    Integer getPortForContainer();

    String getLogOutput();

    boolean isRunning();
}
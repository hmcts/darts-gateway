package uk.gov.hmcts.darts.workflow.command;

public interface Command {
    void cleanupResources();

    void execute();

    boolean isSuccess();
}

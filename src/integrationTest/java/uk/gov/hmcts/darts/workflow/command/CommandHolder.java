package uk.gov.hmcts.darts.workflow.command;

public interface CommandHolder {

    Command getCommand();

    void setCommand(Command command);
}

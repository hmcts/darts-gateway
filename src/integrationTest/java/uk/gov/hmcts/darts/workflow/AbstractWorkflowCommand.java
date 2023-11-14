package uk.gov.hmcts.darts.workflow;

import org.junit.jupiter.api.AfterEach;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.workflow.command.Command;
import uk.gov.hmcts.darts.workflow.command.CommandHolder;

public abstract class AbstractWorkflowCommand extends IntegrationBase implements CommandHolder {
    private Command command;

    @AfterEach
    void after() {

        if (command != null) {
            command.cleanupResources();
        }
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }
}

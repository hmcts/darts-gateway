package uk.gov.hmcts.darts.workflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

@org.testcontainers.junit.jupiter.Testcontainers(disabledWithoutDocker=true)
class AddAudioMtomMidTierWorkflowTest extends AbstractWorkflowCommand {
    @BeforeEach
    void before() throws Exception {
        AddAudioMidTierCommand audioMidTierCommand = CommandFactory.getAudioCommand(getIpAndPort(),
                AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE);
        setCommand(audioMidTierCommand);
    }

    @Test
    void addAudioTest() throws Exception {
        this.getCommand().executeWithDocker();

        Assertions.assertTrue(getCommand().isSuccess());
    }
}

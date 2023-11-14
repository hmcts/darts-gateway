package uk.gov.hmcts.darts.workflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

class AddAudioMtomMidTierWorkflowTest extends AbstractWorkflowCommand {
    @BeforeEach
    void before() throws Exception {
        AddAudioMidTierCommand audioMidTierCommand = CommandFactory.getAudioCommand(getIpAndPort(), AddAudioMidTierCommand.SAMPLE_FILE);
        setCommand(audioMidTierCommand);
    }

    //This test depends on java 1.8 being installed and
    // the environment variable JAVA_18 being set as the underlying add audio mid tier command JRE
    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_18", matches = ".*")
    void addAudioTest() throws Exception {
        //setup the stubbing

        getCommand().execute();

        Assertions.assertTrue(getCommand().isSuccess());
        // verify the stubbing
    }
}

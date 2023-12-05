package uk.gov.hmcts.darts.workflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

@ActiveProfiles("int-test-jwt-token")
@org.testcontainers.junit.jupiter.Testcontainers(disabledWithoutDocker = true)
class AddAudioMtomMidTierWorkflowTest extends AbstractWorkflowCommand {
    @MockBean
    private OauthTokenGenerator generator;

    @BeforeEach
    void before() throws Exception {
        Mockito.when(generator.acquireNewToken(Mockito.anyString(), Mockito.anyString())).thenReturn("test");

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

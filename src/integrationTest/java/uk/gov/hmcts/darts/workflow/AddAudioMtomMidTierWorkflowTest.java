package uk.gov.hmcts.darts.workflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

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

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/audios"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        getCommand().executeWithDocker();

        Assertions.assertTrue(getCommand().isSuccess());
    }
}

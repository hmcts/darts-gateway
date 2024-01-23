package uk.gov.hmcts.darts.workflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.matcher.MultipartDartsProxyContentPattern;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

import java.io.File;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

@ActiveProfiles("int-test-jwt-token")
@org.testcontainers.junit.jupiter.Testcontainers(disabledWithoutDocker = true)
class AddAudioMtomMidTierWorkflowTest extends AbstractWorkflowCommand {
    @MockBean
    private TokenGenerator generator;

    @MockBean
    private TokenValidator validator;

    @BeforeEach
    void before() throws Exception {
        Mockito.when(generator.acquireNewToken(Mockito.anyString(), Mockito.anyString())).thenReturn("test");
        Mockito.when(validator.validate(Mockito.anyString())).thenReturn(true);

        AddAudioMidTierCommand audioMidTierCommand = CommandFactory.getAudioCommand(getIpAndPort(),
                AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE);
        setCommand(audioMidTierCommand);
    }

    @Test
    void addAudioTest() throws Exception {

        File homeDirForTempFiles = new File(System.getProperty("user.home"));
        final int fileCountBefore = Objects.requireNonNull(homeDirForTempFiles.list()).length;
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/audios"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        getCommand().executeWithDocker();

        homeDirForTempFiles = new File(System.getProperty("user.home"));
        int fileCountAfter = Objects.requireNonNull(homeDirForTempFiles.list()).length;

        Assertions.assertTrue(getCommand().isSuccess());
        Assertions.assertEquals(fileCountBefore, fileCountAfter);

        verify(postRequestedFor(urlPathEqualTo("/audios"))
                .withRequestBody(new MultipartDartsProxyContentPattern()));
    }
}

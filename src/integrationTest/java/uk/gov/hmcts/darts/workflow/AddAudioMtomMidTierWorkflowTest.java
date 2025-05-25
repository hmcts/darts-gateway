package uk.gov.hmcts.darts.workflow;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.common.utils.TestUtils;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("int-test-jwt-token-error-log-level")
@org.testcontainers.junit.jupiter.Testcontainers(disabledWithoutDocker = true)
class AddAudioMtomMidTierWorkflowTest extends AbstractWorkflowCommand {
    @MockitoBean
    private TokenGenerator generator;

    @BeforeEach
    void before() {
        doReturn(DEFAULT_TOKEN).when(authenticationCacheService).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        doNothing().when(authenticationCacheService).validateToken(DEFAULT_TOKEN);

        AddAudioMidTierCommand audioMidTierCommand = CommandFactory.getAudioCommand(
            getIpAndPort(),
            AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE,
            DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD
        );
        setCommand(audioMidTierCommand);
    }

    @Test
    void addAudioTest() throws Exception {
        File homeDirForTempFiles = new File(System.getProperty("user.home"));
        final int fileCountBefore = Objects.requireNonNull(homeDirForTempFiles.list()).length;
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/audios/metadata"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        getCommand().executeWithDocker(getCommand().getArguments());
        Assertions.assertTrue(getCommand().getLogOutput().contains("Code: 200"));

        homeDirForTempFiles = new File(System.getProperty("user.home"));
        int fileCountAfter = Objects.requireNonNull(homeDirForTempFiles.list()).length;

        Assertions.assertTrue(getCommand().isSuccess());
        Assertions.assertEquals(fileCountBefore, fileCountAfter);

        verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
            WireMock.matching(
                "\\{\"started_at\":1698048000.000000000,\"ended_at\":1698051600.000000000,\"channel\":0,\"total_channels\":3,\"format\":\"mp2\"," +
                    "\"filename\":\"sample6.mp2\",\"courthouse\":\"CARDIFF\",\"courtroom\":\"1\",\"media_file\":\"sample6.mp2\",\"file_size\":5854354," +
                    "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"123456789\"]," +
                    "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

        verify(postRequestedFor(urlPathEqualTo("/audios/metadata"))
                   .withHeader("Authorization", new RegexPattern("Bearer test")));
    }
}
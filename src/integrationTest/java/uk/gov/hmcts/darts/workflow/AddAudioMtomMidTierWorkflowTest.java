package uk.gov.hmcts.darts.workflow;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.service.Token;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token-error-log-level")
@org.testcontainers.junit.jupiter.Testcontainers(disabledWithoutDocker = true)
class AddAudioMtomMidTierWorkflowTest extends AbstractWorkflowCommand {
    @MockBean
    private TokenGenerator generator;

    @MockBean
    private TokenValidator validator;

    @BeforeEach
    void before() {
        when(generator.acquireNewToken(Mockito.anyString(), Mockito.anyString())).thenReturn("test");
        when(validator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        when(validator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        AddAudioMidTierCommand audioMidTierCommand = CommandFactory.getAudioCommand(getIpAndPort(),
                                                                                    AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE,
                                                                                    "testuser", "testpassword");
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
                    "\"checksum\":null,\"cases\":\\[\"123456789\"],\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

        Mockito.verify(validator, times(2)).test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.anyString());
        Mockito.verify(validator, times(3)).test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.anyString());

        verify(postRequestedFor(urlPathEqualTo("/audios/metadata"))
                   .withHeader("Authorization", new RegexPattern("Bearer test")));
    }
}
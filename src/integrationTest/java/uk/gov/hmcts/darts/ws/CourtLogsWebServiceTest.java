package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.model.event.CourtLog;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token")
class CourtLogsWebServiceTest extends IntegrationBase {

    private static final String VALID_GET_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-get-courtlogs.xml";
    private static final String INVALID_COURTLOGS_XML = "classpath:payloads/events/invalid-soap-message.xml";
    private static final String VALID_POST_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-post-courtlogs.xml";
    private static final String SOME_CASE_NUMBER = "some-case-number";
    private static final String SOME_COURTHOUSE = "some-courthouse";

    private @Value(VALID_GET_COURTLOGS_XML) Resource getCourtLogs;

    private @Value(INVALID_COURTLOGS_XML) Resource invalidSoapMessage;

    private @Value(VALID_POST_COURTLOGS_XML) Resource postCourtLogs;

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken("some-user", "some-password"))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesGetCourtLogRequest(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        var dartsApiCourtLogsResponse = someListOfCourtLog(3);
        courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

        SoapAssertionUtil<GetCourtLogResponse> response = client.getCourtLogs(
            getGatewayUri(),
            getCourtLogs.getContentAsString(
                Charset.defaultCharset())
        );

        com.synapps.moj.dfs.response.GetCourtLogResponse actualResponse = response.getResponse().getValue().getReturn();

        Assertions.assertEquals("200", actualResponse.getCode());
        Assertions.assertEquals("OK", actualResponse.getMessage());
        Assertions.assertEquals(SOME_COURTHOUSE, actualResponse.getCourtLog().getCourthouse());
        Assertions.assertEquals(SOME_CASE_NUMBER, actualResponse.getCourtLog().getCaseNumber());
        Assertions.assertEquals("some-log-text-1", actualResponse.getCourtLog().getEntry().get(0).getValue());
        Assertions.assertEquals("some-log-text-2", actualResponse.getCourtLog().getEntry().get(1).getValue());
        Assertions.assertEquals("some-log-text-3", actualResponse.getCourtLog().getEntry().get(2).getValue());

        courtLogsApi.verifyReceivedGetCourtLogsRequestFor(SOME_COURTHOUSE, "some-case");

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    @SuppressWarnings("PMD.LawOfDemeter")
    void rejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        courtLogsApi.returnsCourtLogs(someListOfCourtLog(1));

        org.assertj.core.api.Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            Charset charset = Charset.defaultCharset();
            String invalidMessage = invalidSoapMessage.getContentAsString(charset);
            client.getCourtLogs(getGatewayUri(), invalidMessage);
        });

        courtLogsApi.verifyDoesntReceiveRequest();

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void postCourtLogsRoute(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        postCourtLogsApi.returnsEventResponse();
        client.postCourtLogs(getGatewayUri(), postCourtLogs.getContentAsString(Charset.defaultCharset()));

        postCourtLogsApi.verifyReceivedPostCourtLogsRequestForCaseNumber("CASE000001");

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void postCourtLogsRouteFailOnInvalidServiceResponse(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        postCourtLogsApi.returnsFailureWhenAddingCourtLogs();

        SoapAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(
            getGatewayUri(),
            postCourtLogs.getContentAsString(Charset.defaultCharset())
        );
        SoapAssertionUtil.assertErrorResponse("404", "Courthouse Not Found",
                                              response.getResponse().getValue().getReturn()
        );

        postCourtLogsApi.verifyReceivedPostCourtLogsRequestForCaseNumber("CASE000001");

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void postCourtLogsRejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        postCourtLogsApi.returnsEventResponse();

        SoapAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(
            getGatewayUri(),
            invalidSoapMessage.getContentAsString(Charset.defaultCharset())
        );
        SoapAssertionUtil.assertErrorResponse("400", "Invalid XML Document",
                                              response.getResponse().getValue().getReturn()
        );

        postCourtLogsApi.verifyDoesntReceiveRequest();
        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }


    private static List<CourtLog> someListOfCourtLog(int numberOfEntries) {
        return IntStream.rangeClosed(1, numberOfEntries)
            .mapToObj((index) -> courtLog(index))
            .collect(toList());
    }

    private static CourtLog courtLog(int index) {
        var courtLog = new CourtLog();
        courtLog.setCaseNumber(SOME_CASE_NUMBER);
        courtLog.setCourthouse(SOME_COURTHOUSE);
        courtLog.setTimestamp(OffsetDateTime.now().minusDays(index));
        courtLog.setEventText("some-log-text-" + index);
        return courtLog;
    }
}

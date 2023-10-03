package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.model.event.CourtLog;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.client.ClientProvider;
import uk.gov.hmcts.darts.utils.client.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.client.DartsGatewayClient;

import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class CourtLogsWebServiceTest extends IntegrationBase {

    private static final String VALID_GET_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-get-courtlogs.xml";
    private static final String INVALID_COURTLOGS_XML = "classpath:payloads/events/invalid-soap-message.xml";
    private static final String VALID_POST_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-post-courtlogs.xml";
    private static final String SOME_CASE_NUMBER = "some-case-number";
    private static final String SOME_COURTHOUSE = "some-courthouse";

    private @Value(VALID_GET_COURTLOGS_XML) Resource getCourtLogs;

    private @Value(INVALID_COURTLOGS_XML) Resource invalidSoapMessage;

    private @Value(VALID_POST_COURTLOGS_XML) Resource postCourtLogs;

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void routesGetCourtLogRequest(DartsGatewayClient client) throws Exception {
        var dartsApiCourtLogsResponse = someListOfCourtLog(3);
        courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

        DartsGatewayAssertionUtil<GetCourtLogResponse> response = client.getCourtLogs(getGatewayUri(),
                                                                                      getCourtLogs.getContentAsString(
                                                                                          Charset.defaultCharset()));

        com.synapps.moj.dfs.response.GetCourtLogResponse actualResponse = response.getResponse().getValue().getReturn();

        Assertions.assertEquals("200", actualResponse.getCode());
        Assertions.assertEquals("OK", actualResponse.getMessage());
        Assertions.assertEquals(SOME_COURTHOUSE, actualResponse.getCourtLog().getCourthouse());
        Assertions.assertEquals(SOME_CASE_NUMBER, actualResponse.getCourtLog().getCaseNumber());
        Assertions.assertEquals("some-log-text-1", actualResponse.getCourtLog().getEntry().get(0).getValue());
        Assertions.assertEquals("some-log-text-2", actualResponse.getCourtLog().getEntry().get(1).getValue());
        Assertions.assertEquals("some-log-text-3", actualResponse.getCourtLog().getEntry().get(2).getValue());

        courtLogsApi.verifyReceivedGetCourtLogsRequestFor(SOME_COURTHOUSE, "some-case");
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    @SuppressWarnings("PMD.LawOfDemeter")
    void rejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {
        courtLogsApi.returnsCourtLogs(someListOfCourtLog(1));

        org.assertj.core.api.Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            Charset chartset = Charset.defaultCharset();
            String invalidMessage = invalidSoapMessage.getContentAsString(chartset);
            client.getCourtLogs(getGatewayUri(), invalidMessage);
        });

        courtLogsApi.verifyDoesntReceiveRequest();
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void postCourtLogsRoute(DartsGatewayClient client) throws Exception {
        postCourtLogsApi.returnsEventResponse();
        client.postCourtLogs(getGatewayUri(), postCourtLogs.getContentAsString(Charset.defaultCharset()));

        postCourtLogsApi.verifyReceivedPostCourtLogsRequestForCaseNumber("CASE000001");
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void postCourtLogsRouteFailOnInvalidServiceResponse(DartsGatewayClient client) throws Exception {
        postCourtLogsApi.returnsFailureWhenAddingCourtLogs();

        DartsGatewayAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(getGatewayUri(),
                                                                                       postCourtLogs.getContentAsString(Charset.defaultCharset()));
        DartsGatewayAssertionUtil.assertErrorResponse("404", "Courthouse Not Found",
                                                      response.getResponse().getValue().getReturn());

        postCourtLogsApi.verifyReceivedPostCourtLogsRequestForCaseNumber("CASE000001");
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void postCourtLogsRejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {
        postCourtLogsApi.returnsEventResponse();

        DartsGatewayAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(getGatewayUri(),
                                                                                       invalidSoapMessage.getContentAsString(Charset.defaultCharset()));
        DartsGatewayAssertionUtil.assertErrorResponse("400", "Invalid XML Document",
                                                      response.getResponse().getValue().getReturn());

        postCourtLogsApi.verifyDoesntReceiveRequest();
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

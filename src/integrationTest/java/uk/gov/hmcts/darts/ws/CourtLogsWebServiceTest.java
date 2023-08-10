package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.test.server.MockWebServiceClient;
import uk.gov.hmcts.darts.model.events.CourtLog;
import uk.gov.hmcts.darts.utils.IntegrationBase;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class CourtLogsWebServiceTest extends IntegrationBase {

    private static final String VALID_GET_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-get-courtlogs.xml";
    private static final String INVALID_GET_COURTLOGS_XML = "classpath:payloads/events/invalid-soap-message.xml";
    private static final String SOME_CASE_NUMBER = "some-case-number";
    private static final String SOME_COURTHOUSE = "some-courthouse";

    @Autowired
    private MockWebServiceClient wsClient;

    @Test
    void routesGetCourtLogRequest(@Value(VALID_GET_COURTLOGS_XML) Resource getCourtLogs) throws IOException {
        var dartsApiCourtLogsResponse = someListOfCourtLog(3);
        courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

        wsClient.sendRequest(withPayload(getCourtLogs))
              .andExpect(noFault())
              .andExpect(xpath("//code").evaluatesTo("200"))
              .andExpect(xpath("//message").evaluatesTo("OK"))
              .andExpect(xpath("//court_log/@courthouse").evaluatesTo(SOME_COURTHOUSE))
              .andExpect(xpath("//court_log/@case_number").evaluatesTo(SOME_CASE_NUMBER))
              .andExpect(xpath("//court_log/entry[1]").evaluatesTo("some-log-text-1"))
              .andExpect(xpath("//court_log/entry[2]").evaluatesTo("some-log-text-2"))
              .andExpect(xpath("//court_log/entry[3]").evaluatesTo("some-log-text-3"));

        courtLogsApi.verifyReceivedGetCourtLogsRequestFor(SOME_COURTHOUSE, "some-case");
    }

    @Test
    void rejectsInvalidSoapMessage(@Value(INVALID_GET_COURTLOGS_XML) Resource invalidSoapMessage) throws IOException {
        courtLogsApi.returnsCourtLogs(someListOfCourtLog(1));

        wsClient.sendRequest(withPayload(invalidSoapMessage))
              .andExpect(clientOrSenderFault());

        courtLogsApi.verifyDoesntReceiveRequest();
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

package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import uk.gov.hmcts.darts.model.courtLogs.CourtLogs;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class GetCourtLogsApiStub extends DartsApiStub {

    private static final String GET_COURT_LOGS_API_PATH = "/courtlogs";

    public void returnsCourtLogs(CourtLogs courtLogs) throws JsonProcessingException {
        stubFor(get(urlPathEqualTo(GET_COURT_LOGS_API_PATH))
              .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(courtLogs))));
    }

    public void verifyDoesntReceiveRequest() {
        verify(exactly(0), getRequestedFor(urlEqualTo(GET_COURT_LOGS_API_PATH)));
    }

    public void verifyReceivedGetCourtLogsRequestFor(String courthouse, String caseNumber) {
        verify(exactly(1), getRequestedFor(urlPathEqualTo(GET_COURT_LOGS_API_PATH))
              .withQueryParam("courthouse", matching(courthouse))
              .withQueryParam("caseNumber", matching(caseNumber))
              .withQueryParam("startDateTime", matching("^.*$"))
              .withQueryParam("endDateTime",  matching("^.*$")));
    }

    public void clearStubs() {
        WireMock.reset();
    }
}

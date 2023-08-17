package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.hmcts.darts.model.events.CourtLog;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class GetCourtLogsApiStub extends DartsApiStub {

    private static final String GET_COURT_LOGS_API_PATH = "/courtlogs";

    public GetCourtLogsApiStub() {
        super(GET_COURT_LOGS_API_PATH);
    }

    public void returnsCourtLogs(List<CourtLog> courtLogs) throws JsonProcessingException {
        stubFor(get(urlPathEqualTo(GET_COURT_LOGS_API_PATH))
              .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(courtLogs))));
    }

    public void verifyReceivedGetCourtLogsRequestFor(String courthouse, String caseNumber) {
        verify(exactly(1), getRequestedFor(urlPathEqualTo(GET_COURT_LOGS_API_PATH))
              .withQueryParam("courthouse", matching(courthouse))
              .withQueryParam("case_number", matching(caseNumber))
              .withQueryParam("start_date_time", matching("^.*$"))
              .withQueryParam("end_date_time",  matching("^.*$")));
    }
}

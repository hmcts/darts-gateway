package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class PostCourtLogsApiStub extends DartsApiStub {

    private static final String POST_COURT_LOGS_API_PATH = "/courtlogs";

    private static final String EVENTS_API_SUCCESS_BODY = """
        {
            "code": "200",
            "message": "OK"
        }""";


    public PostCourtLogsApiStub() {
        super(POST_COURT_LOGS_API_PATH);
    }

    public void returnsEventResponse() throws JsonProcessingException {
        stubFor(post(urlPathEqualTo(POST_COURT_LOGS_API_PATH)).willReturn(aResponse().withHeader(
            "Content-Type",
            "application/json"
        ).withBody(EVENTS_API_SUCCESS_BODY)));
    }

    public void verifyReceivedPostCourtLogsRequestForCaseNumber(String caseNumber) {
        verify(exactly(1), postRequestedFor(urlEqualTo(POST_COURT_LOGS_API_PATH))
            .withRequestBody(containing(caseNumber)));
    }

}
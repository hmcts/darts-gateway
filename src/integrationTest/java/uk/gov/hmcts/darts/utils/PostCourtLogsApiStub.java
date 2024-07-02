package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.io.IOException;

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
            "code": "201",
            "message": "CREATED"
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

    public void returnsFailureWhenAddingCourtLogs() throws JsonProcessingException, IOException {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/courtlogs/problemResponse.json");

        stubFor(post(urlPathEqualTo(POST_COURT_LOGS_API_PATH)).willReturn(aResponse().withHeader(
            "Content-Type",
            "application/json"
        ).withStatus(404).withBody(dartsApiResponseStr)));
    }
}

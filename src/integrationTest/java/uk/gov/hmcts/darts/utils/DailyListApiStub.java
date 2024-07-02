package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class DailyListApiStub extends DartsApiStub {

    private static final String DAILY_LIST_API_PATH = "/dailylists";

    public DailyListApiStub() {
        super(DAILY_LIST_API_PATH);
    }

    public void willRespondSuccessfully() {
        stubFor(post(urlPathEqualTo(DAILY_LIST_API_PATH))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("{\"dal_id\":1}")));
        stubFor(patch(urlPathEqualTo(DAILY_LIST_API_PATH))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("{\"dal_id\":1}")));
    }

    public void verifyPostRequest() throws IOException {
        String dailyListXmlString = TestUtils.getContentsFromFile(
            "payloads/events/dailyList-api-post-request.json");
        verify(exactly(1), postRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
            .withRequestBody(equalToJson(dailyListXmlString))
        );
    }

    public void verifyPostRequestWithoutLineBreaks() throws IOException {
        String dailyListXmlString = TestUtils.getContentsFromFile(
            "payloads/events/dailyList-api-request-with-lb-removed.json");
        verify(exactly(1), postRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
            .withRequestBody(equalToJson(dailyListXmlString))
        );
    }

    public void verifyPatchRequest() throws IOException {
        String dailyListJsonString = TestUtils.getContentsFromFile(
            "payloads/events/dailyList-api-patch-request.json");

        verify(exactly(1), patchRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
            .withRequestBody(equalToJson(dailyListJsonString))
        );
    }

    public void verifyCppPatchRequest() throws IOException {
        String dailyListJsonString = TestUtils.getContentsFromFile("payloads/events/dailyList-CPP-api-patch-request.json");

        verify(exactly(1), patchRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
            .withRequestBody(equalToJson(dailyListJsonString))
        );
    }

    public void returnsFailureWhenPostingDailyList() throws JsonProcessingException, IOException {
        returnsFailureWhenPostingDailyList(true);
    }

    public void returnsFailureWhenPostingDailyList(boolean withBody) throws JsonProcessingException, IOException {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/events/problemDailyListResponse.json");

        if (withBody) {
            stubFor(post(urlPathEqualTo(DAILY_LIST_API_PATH))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withStatus(404)
                            .withBody(dartsApiResponseStr)));
        } else {
            stubFor(post(urlPathEqualTo(DAILY_LIST_API_PATH))
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withHeader("Content-Type", "application/json")));
        }
    }
}

package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
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
            "payloads/events/dailyList-api-request.xml");
        verify(exactly(1), postRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
            .withQueryParam("source_system", equalTo("XHB"))
            .withQueryParam("courthouse", equalTo("SNARESBROOK"))
            .withQueryParam("hearing_date", equalTo("2010-02-18"))
            .withQueryParam("unique_id", equalTo("CSDDL000000000576147"))
            .withQueryParam("published_ts", equalTo("2010-02-17T16:16:50Z"))
            .withHeader("xml_document", equalTo(StringEscapeUtils.unescapeXml(dailyListXmlString.trim())))
        );
    }

    public void verifyPatchRequest() throws IOException {
        String dailyListJsonString = TestUtils.getContentsFromFile(
            "payloads/events/dailyList-api-request.json");

        verify(exactly(1), patchRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
            .withQueryParam("dal_id", equalTo("1"))
            .withHeader("json_string", equalTo(dailyListJsonString.trim()))
        );
    }

    public void returnsFailureWhenPostingDailyList() throws JsonProcessingException, IOException {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/events/problemDailyListResponse.json");

        stubFor(post(urlPathEqualTo(DAILY_LIST_API_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                        .withBody(dartsApiResponseStr)));
    }
}

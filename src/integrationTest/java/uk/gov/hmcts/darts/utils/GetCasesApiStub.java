package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;


public class GetCasesApiStub {
    public void returnsFailureWhenGettingCases() throws JsonProcessingException, IOException {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/problemResponse.json");

        stubFor(get(urlPathEqualTo("/cases"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody(dartsApiResponseStr)));
    }
}

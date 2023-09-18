package uk.gov.hmcts.darts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class AddCasesApiStub {
    public void returnsFailureWhenPostingNewCase() throws JsonProcessingException, IOException {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/problemResponse.json");

        stubFor(post(urlPathEqualTo("/cases"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody(dartsApiResponseStr)));
    }
}

package uk.gov.hmcts.darts.testutils.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

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

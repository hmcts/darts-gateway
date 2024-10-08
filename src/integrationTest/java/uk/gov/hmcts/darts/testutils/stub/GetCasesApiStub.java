package uk.gov.hmcts.darts.testutils.stub;

import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;


public class GetCasesApiStub {
    public void returnsFailureWhenGettingCases() throws IOException {
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/problemResponse.json");

        stubFor(get(urlPathEqualTo("/cases"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody(dartsApiResponseStr)));
    }
}

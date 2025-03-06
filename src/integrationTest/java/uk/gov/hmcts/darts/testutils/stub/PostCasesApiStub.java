package uk.gov.hmcts.darts.testutils.stub;

import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;


public class PostCasesApiStub {

    private static final String CASE_API_PATH = "/cases";

    public void willRespondSuccessfully() {
        stubFor(post(urlEqualTo(CASE_API_PATH))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withStatus(200)));
    }

    public void verifyPostRequest(String requestFilePath) throws IOException {
        String eventJson = TestUtils.getContentsFromFile(requestFilePath);
        verify(exactly(1), postRequestedFor(urlEqualTo(CASE_API_PATH))
            .withRequestBody(equalToJson(eventJson)));
    }
}

package uk.gov.hmcts.darts.testutils.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class TokenStub {
    public void stubExternalToken(String token) {
        stubFor(post(urlPathEqualTo("/token"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("""
                                                          {"access_token":"%s","token_type":"Bearer","expires_in":"3600"}
                                                      """.formatted(token))));
    }


    @SuppressWarnings("checkstyle:linelength")
    public void stubExternalJwksKeys(String keys) {
        stubFor(get(urlPathEqualTo("/keys"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("""
                                                      {"keys":[%s]}
                                                      """.formatted(keys))));
    }

    public void verifyNumberOfTimesKeysObtained(int numberOfTimes) {
        verify(exactly(numberOfTimes), getRequestedFor(urlPathEqualTo("/keys")));
    }
}

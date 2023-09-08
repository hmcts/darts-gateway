package uk.gov.hmcts.darts.utils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
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

    public void verifySentRequest() {
        verify(exactly(1), postRequestedFor(urlPathEqualTo(DAILY_LIST_API_PATH))
              .withQueryParam("source_system", equalTo("XHB")));
    }
}

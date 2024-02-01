package uk.gov.hmcts.darts.utils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class EventApiStub extends DartsApiStub {

    private static final String EVENT_API_PATH = "/events";
    private static final String DAILY_LIST_API_PATH = "/dailylists";

    private static final String EVENTS_API_SUCCESS_BODY = """
          {
              "code": "200",
              "message": "OK"
          }""";

    public EventApiStub() {
        super(EVENT_API_PATH);
    }

    public void willRespondSuccessfully() {
        stubFor(post(urlEqualTo(EVENT_API_PATH))
              .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(EVENTS_API_SUCCESS_BODY)));

        stubFor(post(urlPathEqualTo(DAILY_LIST_API_PATH))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(EVENTS_API_SUCCESS_BODY)));
    }

    public void verifyDoesntReceiveEvent() {
        verify(exactly(0), postRequestedFor(urlEqualTo(EVENT_API_PATH)));
    }

    public void verifyPostRequest() throws IOException {
        String eventJson = TestUtils.getContentsFromFile("payloads/events/valid-event-api-request.json");
        verify(exactly(1), postRequestedFor(urlEqualTo(EVENT_API_PATH))
            .withRequestBody(equalToJson(eventJson)));
    }
}

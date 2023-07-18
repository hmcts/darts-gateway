package uk.gov.hmcts.darts.utils;

import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class EventApiStub {

    private static final String EVENT_API_PATH = "/events";

    private static final String EVENTS_API_SUCCESS_BODY = """
          {
              "code": "200",
              "message": "OK"
          }""";

    public void willRespondSuccessfully() {
        stubFor(post(urlEqualTo(EVENT_API_PATH))
              .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(EVENTS_API_SUCCESS_BODY)));
    }

    public void verifyDoesntReceiveEvent() {
        verify(exactly(0), postRequestedFor(urlEqualTo(EVENT_API_PATH)));
    }

    public void verifyReceivedEventWithMessageId(String messageId) {
        var messageIdField = "\"messageId\":\"" + messageId + "\"";
        verify(exactly(1), postRequestedFor(urlEqualTo(EVENT_API_PATH))
              .withRequestBody(containing(messageIdField)));
    }

    public void clearStubs() {
        WireMock.reset();
    }
}

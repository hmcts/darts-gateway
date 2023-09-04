package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseActions;
import org.xmlunit.matchers.CompareMatcher;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class EventWebServiceTest extends IntegrationBase {

    @Autowired
    private MockWebServiceClient wsClient;

    @Test
    void routesValidEventPayload(
          @Value("classpath:payloads/events/valid-event.xml") Resource validEvent,
          @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse
    ) throws IOException {
        theEventApi.willRespondSuccessfully();

        ResponseActions responseActions = wsClient.sendRequest(withPayload(validEvent))
            .andExpect(noFault());
        String actualResponse = TestUtils.getResponse(responseActions);
        assertThat(actualResponse, CompareMatcher.isSimilarTo(validEventResponse.getContentAsString(Charset.defaultCharset())).ignoreWhitespace());

        theEventApi.verifyReceivedEventWithMessageId("12345");
    }

    @Test
    void rejectsInvalidSoapMessage(@Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage) throws IOException {
        theEventApi.willRespondSuccessfully();

        wsClient.sendRequest(withPayload(invalidSoapMessage))
              .andExpect(clientOrSenderFault());

        theEventApi.verifyDoesntReceiveEvent();
    }

    @Test
    void routesValidDailyListPayload(
        @Value("classpath:payloads/events/valid-dailyList.xml") Resource validEvent,
        @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse
    ) throws IOException {
        dailyListApiStub.willRespondSuccessfully();

        ResponseActions responseActions = wsClient.sendRequest(withPayload(validEvent))
            .andExpect(noFault());
        String actualResponse = TestUtils.getResponse(responseActions);
        String expectedResponse = validEventResponse.getContentAsString(Charset.defaultCharset());
        assertThat(actualResponse, CompareMatcher.isSimilarTo(expectedResponse).ignoreWhitespace());

        dailyListApiStub.verifySentRequest();
    }

    @Test
    void routesInvalidDailyListPayload(
        @Value("classpath:payloads/events/invalid-dailyList.xml") Resource invalidDailyListRequest,
        @Value("classpath:payloads/events/invalid-dailyList-response.xml") Resource expectedResponse
    ) throws IOException {
        dailyListApiStub.willRespondSuccessfully();

        ResponseActions responseActions = wsClient.sendRequest(withPayload(invalidDailyListRequest));

        String actualResponse = TestUtils.getResponse(responseActions);
        String expectedResponseStr = expectedResponse.getContentAsString(Charset.defaultCharset());
        assertThat(actualResponse, CompareMatcher.isSimilarTo(expectedResponseStr).ignoreWhitespace());

    }


}

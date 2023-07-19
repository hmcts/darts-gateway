package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.test.server.MockWebServiceClient;
import uk.gov.hmcts.darts.utils.IntegrationBase;

import java.io.IOException;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

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

        wsClient.sendRequest(withPayload(validEvent))
            .andExpect(noFault())
            .andExpect(payload(validEventResponse));

        theEventApi.verifyReceivedEventWithMessageId("12345");
    }

    @Test
    void rejectsInvalidSoapMessage(@Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage) throws IOException {
        theEventApi.willRespondSuccessfully();

        wsClient.sendRequest(withPayload(invalidSoapMessage))
              .andExpect(clientOrSenderFault());

        theEventApi.verifyDoesntReceiveEvent();
    }
}

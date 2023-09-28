package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseActions;
import org.xmlunit.matchers.CompareMatcher;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.motm.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.motm.DartsGatewayMTOMClient;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class EventWebServiceTest extends IntegrationBase {

    @Autowired
    private DartsGatewayMTOMClient motmClient;

    @Test
    void routesValidEventPayload(
          @Value("classpath:payloads/events/valid-event.xml") Resource validEvent,
          @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse
    ) throws Exception {
        theEventApi.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = motmClient.addDocument(getGatewayURI(), validEvent.getContentAsString(Charset.defaultCharset()));
        response.assertIdenticalResponse(motmClient.convertData(validEventResponse.getContentAsString(Charset.defaultCharset()), AddDocumentResponse.class).getValue());

        theEventApi.verifyReceivedEventWithMessageId("12345");
    }

    //@Test
    void rejectsInvalidSoapMessage(@Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage) throws Exception {
        theEventApi.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = motmClient.addDocument(getGatewayURI(), invalidSoapMessage.getContentAsString(Charset.defaultCharset()));
        DartsGatewayAssertionUtil.assertErrorResponse("200", "OK", response.getResponse().getValue().getReturn());

        theEventApi.verifyDoesntReceiveEvent();
    }

    @Test
    void routesValidDailyListPayload(
        @Value("classpath:payloads/events/valid-dailyList.xml") Resource validEvent,
        @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse
    ) throws Exception {
        dailyListApiStub.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = motmClient.addDocument(getGatewayURI(), validEvent.getContentAsString(Charset.defaultCharset()));
        response.assertIdenticalResponse(motmClient.convertData(validEventResponse.getContentAsString(Charset.defaultCharset()), AddDocumentResponse.class).getValue());

        dailyListApiStub.verifySentRequest();
    }

    @Test
    void routesValidDailyListPayloadWithInvalidServiceResponse(
            @Value("classpath:payloads/events/valid-dailyList.xml") Resource validEvent,
            @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse
    ) throws IOException {
        dailyListApiStub.returnsFailureWhenPostingDailyList();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(()->
        {
            motmClient.addDocument(getGatewayURI(), validEventResponse.getContentAsString(Charset.defaultCharset()));
        });
    }

    @Test
    void routesInvalidDailyListPayload(
        @Value("classpath:payloads/events/invalid-dailyList.xml") Resource invalidDailyListRequest,
        @Value("classpath:payloads/events/invalid-dailyList-response.xml") Resource expectedResponse
    ) throws Exception {
        dailyListApiStub.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = motmClient.addDocument(getGatewayURI(), invalidDailyListRequest.getContentAsString(Charset.defaultCharset()));
        response.assertIdenticalResponse(motmClient.convertData(expectedResponse.getContentAsString(Charset.defaultCharset()), AddDocumentResponse.class).getValue());
    }
}

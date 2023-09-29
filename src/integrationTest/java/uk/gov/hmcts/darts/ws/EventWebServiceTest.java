package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.client.ClientProvider;
import uk.gov.hmcts.darts.utils.client.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.client.DartsGatewayClientable;
import uk.gov.hmcts.darts.utils.client.DartsGatewayMTOMClient;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class EventWebServiceTest extends IntegrationBase {

    private @Value("classpath:payloads/events/valid-event.xml") Resource validEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse;

    private @Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage;

    private @Value("classpath:payloads/events/valid-dailyList.xml") Resource validDLEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validDLEventResponse;

    private  @Value("classpath:payloads/events/invalid-dailyList.xml") Resource invalidDailyListRequest;
    private @Value("classpath:payloads/events/invalid-dailyList-response.xml") Resource expectedResponse;

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void routesValidEventPayload(
          DartsGatewayClientable client
    ) throws Exception {
        theEventApi.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = client.addDocument(getGatewayURI(), validEvent.getContentAsString(Charset.defaultCharset()));
        response.assertIdenticalResponse(client.convertData(validEventResponse.getContentAsString(Charset.defaultCharset()), AddDocumentResponse.class).getValue());

        theEventApi.verifyReceivedEventWithMessageId("12345");
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void reactsInvalidSoapMessage(
                                  DartsGatewayClientable client) throws Exception {
        theEventApi.willRespondSuccessfully();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(()-> {
            client.send(getGatewayURI(), invalidSoapMessage.getContentAsString(Charset.defaultCharset()));
        });
        theEventApi.verifyDoesntReceiveEvent();
    }

    //TODO: We need to comment this back in when we know what we are doing with the json string header its failing
    // as part of the spring feign data validation annotations
    //@ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void routesValidDailyListPayload(

        DartsGatewayClientable client
    ) throws Exception {
        dailyListApiStub.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = client.addDocument(getGatewayURI(), validDLEvent.getContentAsString(Charset.defaultCharset()));
        response.assertIdenticalResponse(client.convertData(validDLEventResponse.getContentAsString(Charset.defaultCharset()), AddDocumentResponse.class).getValue());

        dailyListApiStub.verifySentRequest();
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void routesValidDailyListPayloadWithInvalidServiceResponse(

            DartsGatewayClientable client
    ) throws IOException {
        dailyListApiStub.returnsFailureWhenPostingDailyList();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(()->
        {
            client.addDocument(getGatewayURI(), validDLEventResponse.getContentAsString(Charset.defaultCharset()));
        });
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void routesInvalidDailyListPayload(

        DartsGatewayClientable client
    ) throws Exception {
        dailyListApiStub.willRespondSuccessfully();

        DartsGatewayAssertionUtil<AddDocumentResponse> response = client.addDocument(getGatewayURI(), invalidDailyListRequest.getContentAsString(Charset.defaultCharset()));
        response.assertIdenticalResponse(client.convertData(expectedResponse.getContentAsString(Charset.defaultCharset()), AddDocumentResponse.class).getValue());
    }
}

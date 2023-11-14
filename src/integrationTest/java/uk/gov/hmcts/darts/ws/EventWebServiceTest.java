package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import java.io.IOException;
import java.nio.charset.Charset;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class EventWebServiceTest extends IntegrationBase {

    private @Value("classpath:payloads/events/valid-event.xml") Resource validEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse;

    private @Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage;

    private @Value("classpath:payloads/events/valid-dailyList.xml") Resource validDlEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validDlEventResponse;

    private  @Value("classpath:payloads/events/invalid-dailyList.xml") Resource invalidDailyListRequest;
    private @Value("classpath:payloads/events/invalid-dailyList-response.xml") Resource expectedResponse;

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesValidEventPayload(
          DartsGatewayClient client
    ) throws Exception {
        theEventApi.willRespondSuccessfully();

        SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(getGatewayUri(),
                                                                                     validEvent.getContentAsString(
                                                                                         Charset.defaultCharset()));
        response.assertIdenticalResponse(client.convertData(validEventResponse.getContentAsString(Charset.defaultCharset()),
                                                            AddDocumentResponse.class).getValue());

        theEventApi.verifyReceivedEventWithMessageId("12345");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void reactsInvalidSoapMessage(
                                  DartsGatewayClient client) throws Exception {
        theEventApi.willRespondSuccessfully();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.send(getGatewayUri(), invalidSoapMessage.getContentAsString(Charset.defaultCharset()));
        });
        theEventApi.verifyDoesntReceiveEvent();
    }

    //TODO: We need to comment this back in when we know what we are doing with the json string header its failing
    // as part of the spring feign data validation annotations
    //@ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesValidDailyListPayload(

        DartsGatewayClient client
    ) throws Exception {
        dailyListApiStub.willRespondSuccessfully();

        SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(getGatewayUri(),
                                                                                     validDlEvent.getContentAsString(
                                                                                         Charset.defaultCharset()));
        response.assertIdenticalResponse(client.convertData(validDlEventResponse.getContentAsString(Charset.defaultCharset()),
                                                            AddDocumentResponse.class).getValue());

        dailyListApiStub.verifySentRequest();
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesValidDailyListPayloadWithInvalidServiceResponse(
            DartsGatewayClient client
    ) throws IOException {
        dailyListApiStub.returnsFailureWhenPostingDailyList();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.addDocument(getGatewayUri(), validDlEventResponse.getContentAsString(Charset.defaultCharset()));
        });
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesInvalidDailyListPayload(

        DartsGatewayClient client
    ) throws Exception {
        dailyListApiStub.willRespondSuccessfully();

        SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(getGatewayUri(),
                                                                                     invalidDailyListRequest.getContentAsString(
                                                                                         Charset.defaultCharset()));
        response.assertIdenticalResponse(client.convertData(expectedResponse.getContentAsString(Charset.defaultCharset()),
                                                            AddDocumentResponse.class).getValue());
    }
}

package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token")
class EventWebServiceTest extends IntegrationBase {

    private @Value("classpath:payloads/events/valid-event.xml") Resource validEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse;

    private @Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage;

    private @Value("classpath:payloads/events/valid-dailyList.xml") Resource validDlEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validDlEventResponse;

    private @Value("classpath:payloads/events/invalid-dailyList.xml") Resource invalidDailyListRequest;
    private @Value("classpath:payloads/events/invalid-dailyList-response.xml") Resource expectedResponse;

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken("some-user", "some-password"))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesValidEventPayload(
        DartsGatewayClient client
    ) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        theEventApi.willRespondSuccessfully();

        SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
            getGatewayUri(),
            validEvent.getContentAsString(
                Charset.defaultCharset())
        );
        response.assertIdenticalResponse(client.convertData(
            validEventResponse.getContentAsString(Charset.defaultCharset()),
            AddDocumentResponse.class
        ).getValue());

        theEventApi.verifyReceivedEventWithMessageId("12345");

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void rejectsInvalidSoapMessage(
        DartsGatewayClient client) throws IOException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        theEventApi.willRespondSuccessfully();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.send(getGatewayUri(), invalidSoapMessage.getContentAsString(Charset.defaultCharset()));
        });
        theEventApi.verifyDoesntReceiveEvent();

        verifyNoInteractions(mockOauthTokenGenerator);
    }

    //TODO: We need to comment this back in when we know what we are doing with the json string header its failing
    // as part of the spring feign data validation annotations
    //@ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesValidDailyListPayload(
        DartsGatewayClient client
    ) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        dailyListApiStub.willRespondSuccessfully();

        SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
            getGatewayUri(),
            validDlEvent.getContentAsString(
                Charset.defaultCharset())
        );
        response.assertIdenticalResponse(client.convertData(
            validDlEventResponse.getContentAsString(Charset.defaultCharset()),
            AddDocumentResponse.class
        ).getValue());

        dailyListApiStub.verifySentRequest();

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesValidDailyListPayloadWithInvalidServiceResponse(
        DartsGatewayClient client
    ) throws IOException {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        dailyListApiStub.returnsFailureWhenPostingDailyList();

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.addDocument(getGatewayUri(), validDlEventResponse.getContentAsString(Charset.defaultCharset()));
        });

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesInvalidDailyListPayload(
        DartsGatewayClient client
    ) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        dailyListApiStub.willRespondSuccessfully();

        SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
            getGatewayUri(),
            invalidDailyListRequest.getContentAsString(
                Charset.defaultCharset())
        );
        response.assertIdenticalResponse(client.convertData(
            expectedResponse.getContentAsString(Charset.defaultCharset()),
            AddDocumentResponse.class
        ).getValue());

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

}

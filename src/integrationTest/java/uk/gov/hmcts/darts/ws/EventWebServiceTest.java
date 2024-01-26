package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.common.client.mapper.DailyListAPIProblemResponseMapper;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import java.nio.charset.Charset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private static final DailyListAPIProblemResponseMapper
        DAILY_LIST_API_PROBLEM_RESPONSE_MAPPER = new DailyListAPIProblemResponseMapper();

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesAddDocumentRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
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
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRouteAddDocumentRequestWithIdentitiesFailure(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
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

        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesAddDocumentRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
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
        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidEventPayloadWithAuthenticationToken(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithTokenHeader(client, () -> {
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
        }, getContextClient(), getGatewayUri(), DEFAULT_USERNAME, DEFAULT_PASSWORD);

        theEventApi.verifyReceivedEventWithMessageId("12345");

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidEventPayload(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
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
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        theEventApi.verifyReceivedEventWithMessageId("12345");

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {

            theEventApi.willRespondSuccessfully();

            Assertions.assertThrows(SoapFaultClientException.class, () -> {
                client.send(getGatewayUri(), invalidSoapMessage.getContentAsString(Charset.defaultCharset()));
            });
            theEventApi.verifyDoesntReceiveEvent();
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidDailyListPayload(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.willRespondSuccessfully();

            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                getGatewayUri(),
                validDlEvent.getContentAsString(Charset.defaultCharset())
            );
            response.assertIdenticalResponse(client.convertData(
                validDlEventResponse.getContentAsString(Charset.defaultCharset()),
                AddDocumentResponse.class
            ).getValue());

            dailyListApiStub.verifyPostRequest();
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        dailyListApiStub.verifyPostRequest();
        dailyListApiStub.verifyPatchRequest();

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidDailyListPayloadWithInvalidServiceResponse(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.returnsFailureWhenPostingDailyList();

            org.assertj.core.api.Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
                client.addDocument(getGatewayUri(), validDlEventResponse.getContentAsString(Charset.defaultCharset()));
            });
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesInvalidDailyListPayload(
        DartsGatewayClient client
    ) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.returnsFailureWhenPostingDailyList();
            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                   getGatewayUri(),
                    validDlEvent.getContentAsString(Charset.defaultCharset())
               );

            ProblemResponseMapping<?> op = getSpecificSoapErrorCode(PostDailyListErrorCode.DAILYLIST_PROCESSOR_NOT_FOUND.getValue(),
                                                                    DAILY_LIST_API_PROBLEM_RESPONSE_MAPPER
            );

            SoapAssertionUtil.assertErrorResponse(op.getMessage().getCode(), op.getMessage().getMessage(), response.getResponse().getValue().getReturn());

        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesInvalidDailyListPayloadNoDartsAPIProblemBody(
            DartsGatewayClient client
    ) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.returnsFailureWhenPostingDailyList(false);
            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                    getGatewayUri(),
                    validDlEvent.getContentAsString(Charset.defaultCharset())
            );

            SoapAssertionUtil.assertErrorResponse(String.valueOf(500), response.getResponse().getValue().getReturn());
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }
}

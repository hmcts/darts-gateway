package uk.gov.hmcts.darts.ws;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.common.client.mapper.DailyListAPIProblemResponseMapper;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.model.dailylist.DailyListErrorCode;
import uk.gov.hmcts.darts.utils.IntegrationBase;

import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token-shared")
class EventWebServiceTest extends IntegrationBase {

    private @Value("classpath:payloads/events/valid-event.xml") Resource validEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validEventResponse;
    private @Value("classpath:payloads/events/valid-event-with-retention.xml") Resource validEventWithRetention;
    private @Value("classpath:payloads/events/invalid-soap-message.xml") Resource invalidSoapMessage;
    private @Value("classpath:payloads/events/valid-dailyList.xml") Resource validDlEvent;
    private @Value("classpath:payloads/events/valid-event-response.xml") Resource validDlEventResponse;
    private @Value("classpath:payloads/events/valid-dailyList-with-line-breaks.xml") Resource dailyListWithLineBreak;

    private static final DailyListAPIProblemResponseMapper
        DAILY_LIST_API_PROBLEM_RESPONSE_MAPPER = new DailyListAPIProblemResponseMapper();

    @MockBean
    private TokenGenerator mockOauthTokenGenerator;

    @MockBean
    private TokenValidator tokenValidator;

    @BeforeEach
    public void before() {
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("test");

        when(mockOauthTokenGenerator.acquireNewToken(ContextRegistryParent.SERVICE_CONTEXT_USER, ContextRegistryParent.SERVICE_CONTEXT_PASSWORD))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesAddDocumentRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        theEventApi.verifyPostRequest("payloads/events/valid-event-api-request.json");

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidEventPayloadWithAuthenticationTokenRefresh(
        DartsGatewayClient client
    ) throws Exception {

        when(tokenValidator.test(Mockito.any(),
                                     Mockito.eq("downstreamtoken"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("downstreamtoken", "test", "downstreamrefresh", "downstreamrefreshoutsidecache");

        authenticationStub.assertWithTokenHeader(client, () -> {
            theEventApi.willRespondSuccessfully();

            when(tokenValidator.test(Mockito.any(),
                                         Mockito.eq("downstreamtoken"))).thenReturn(false);

            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                getGatewayUri(),
                validEvent.getContentAsString(
                    Charset.defaultCharset())
            );

            response.assertIdenticalResponse(client.convertData(
                validEventResponse.getContentAsString(Charset.defaultCharset()),
                AddDocumentResponse.class
            ).getValue());
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        WireMock.verify(postRequestedFor(urlPathEqualTo("/events"))
                            .withHeader("Authorization", new RegexPattern("Bearer downstreamrefreshoutsidecache")));

        verify(mockOauthTokenGenerator, times(4)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        WireMock.verify(postRequestedFor(urlPathEqualTo("/events"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        theEventApi.verifyPostRequest("payloads/events/valid-event-api-request.json");

        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesValidEventWithRetentionPayload(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            theEventApi.willRespondSuccessfully();

            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                getGatewayUri(),
                validEventWithRetention.getContentAsString(
                    Charset.defaultCharset())
            );
            response.assertIdenticalResponse(client.convertData(
                validEventResponse.getContentAsString(Charset.defaultCharset()),
                AddDocumentResponse.class
            ).getValue());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        WireMock.verify(postRequestedFor(urlPathEqualTo("/events"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        theEventApi.verifyPostRequest("payloads/events/valid-event-api-with-retention-request.json");

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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        dailyListApiStub.verifyPostRequest();
        dailyListApiStub.verifyPatchRequest();

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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

            ProblemResponseMapping<?> op = getSpecificSoapErrorCode(
                DailyListErrorCode.FAILED_TO_PROCESS_DAILYLIST.getValue(),
                DAILY_LIST_API_PROBLEM_RESPONSE_MAPPER
            );

            SoapAssertionUtil.assertErrorResponse(op.getMessage().getCode(), op.getMessage().getMessage(), response.getResponse().getValue().getReturn());

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void removesLineBreaksFromDailyListXml(
        DartsGatewayClient client
    ) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.willRespondSuccessfully();

            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                getGatewayUri(),
                dailyListWithLineBreak.getContentAsString(Charset.defaultCharset())
            );
            response.assertIdenticalResponse(client.convertData(
                validDlEventResponse.getContentAsString(Charset.defaultCharset()),
                AddDocumentResponse.class
            ).getValue());

            dailyListApiStub.verifyPostRequestWithoutLineBreaks();
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        dailyListApiStub.verifyPostRequestWithoutLineBreaks();

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void cppDailyListMapsUrnToDefendantInJson(
        DartsGatewayClient client
    ) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            dailyListApiStub.willRespondSuccessfully();

            SoapAssertionUtil<AddDocumentResponse> response = client.addDocument(
                getGatewayUri(),
                dailyListWithLineBreak.getContentAsString(Charset.defaultCharset())
            );
            response.assertIdenticalResponse(client.convertData(
                validDlEventResponse.getContentAsString(Charset.defaultCharset()),
                AddDocumentResponse.class
            ).getValue());

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        dailyListApiStub.verifyCppPatchRequest();
    }
}

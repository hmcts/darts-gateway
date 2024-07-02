package uk.gov.hmcts.darts.ws;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
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
import uk.gov.hmcts.darts.model.event.CourtLog;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token-shared")
class CourtLogsWebServiceTest extends IntegrationBase {

    private static final String VALID_GET_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-get-courtlogs.xml";
    private static final String INVALID_COURTLOGS_XML = "classpath:payloads/events/invalid-soap-message.xml";
    private static final String VALID_POST_COURTLOGS_XML = "classpath:payloads/courtlogs/valid-post-courtlogs.xml";
    private static final String SOME_CASE_NUMBER = "some-case-number";
    private static final String SOME_COURTHOUSE = "some-courthouse";

    private @Value(VALID_GET_COURTLOGS_XML) Resource getCourtLogs;

    private @Value(INVALID_COURTLOGS_XML) Resource invalidSoapMessage;

    private @Value(VALID_POST_COURTLOGS_XML) Resource postCourtLogs;

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
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCourtLogsRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            List<CourtLog> dartsApiCourtLogsResponse = someListOfCourtLog(3);
            courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

            client.getCourtLogs(
                getGatewayUri(),
                getCourtLogs.getContentAsString(
                   Charset.defaultCharset())
            );
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithIdentitiesFailure(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            List<CourtLog> dartsApiCourtLogsResponse = someListOfCourtLog(3);
            courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

            client.getCourtLogs(
                getGatewayUri(),
                getCourtLogs.getContentAsString(
                    Charset.defaultCharset())
            );

        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            List<CourtLog> dartsApiCourtLogsResponse = someListOfCourtLog(3);
            courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

            client.getCourtLogs(
                getGatewayUri(),
                getCourtLogs.getContentAsString(
                    Charset.defaultCharset())
            );
        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCourtLogRequestWithAuthenticationToken(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            List<CourtLog> dartsApiCourtLogsResponse = someListOfCourtLog(3);
            courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

            SoapAssertionUtil<GetCourtLogResponse> response = client.getCourtLogs(
                getGatewayUri(),
                getCourtLogs.getContentAsString(
                    Charset.defaultCharset())
            );

            com.synapps.moj.dfs.response.GetCourtLogResponse actualResponse = response.getResponse().getValue().getReturn();

            assertEquals("200", actualResponse.getCode());
            assertEquals("OK", actualResponse.getMessage());
            assertEquals(SOME_COURTHOUSE, actualResponse.getCourtLog().getCourthouse());
            assertEquals(SOME_CASE_NUMBER, actualResponse.getCourtLog().getCaseNumber());
            assertEquals("some-log-text-1", actualResponse.getCourtLog().getEntry().get(0).getValue());
            assertEquals("some-log-text-2", actualResponse.getCourtLog().getEntry().get(1).getValue());
            assertEquals("some-log-text-3", actualResponse.getCourtLog().getEntry().get(2).getValue());
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        courtLogsApi.verifyReceivedGetCourtLogsRequestFor(SOME_COURTHOUSE, "some-case");

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCourtLogRequestWithAuthenticationTokenRefresh(DartsGatewayClient client) throws Exception {

        when(tokenValidator.test(Mockito.any(),
                                Mockito.eq("downstreamtoken"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("downstreamtoken", "test", "downstreamrefresh", "downstreamrefreshoutsidecache");


        authenticationStub.assertWithTokenHeader(client, () -> {
            List<CourtLog> dartsApiCourtLogsResponse = someListOfCourtLog(3);
            courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

            when(tokenValidator.test(Mockito.any(),
                                         Mockito.eq("downstreamtoken"))).thenReturn(false);

            SoapAssertionUtil<GetCourtLogResponse> response = client.getCourtLogs(
                getGatewayUri(),
                getCourtLogs.getContentAsString(
                    Charset.defaultCharset())
            );

            com.synapps.moj.dfs.response.GetCourtLogResponse actualResponse = response.getResponse().getValue().getReturn();

            assertEquals("200", actualResponse.getCode());
            assertEquals("OK", actualResponse.getMessage());
            assertEquals(SOME_COURTHOUSE, actualResponse.getCourtLog().getCourthouse());
            assertEquals(SOME_CASE_NUMBER, actualResponse.getCourtLog().getCaseNumber());
            assertEquals("some-log-text-1", actualResponse.getCourtLog().getEntry().get(0).getValue());
            assertEquals("some-log-text-2", actualResponse.getCourtLog().getEntry().get(1).getValue());
            assertEquals("some-log-text-3", actualResponse.getCourtLog().getEntry().get(2).getValue());
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        courtLogsApi.verifyReceivedGetCourtLogsRequestFor(SOME_COURTHOUSE, "some-case");

        WireMock.verify(getRequestedFor(urlMatching("/courtlogs.*"))
                            .withHeader("Authorization", new RegexPattern("Bearer downstreamrefreshoutsidecache")));

        verify(mockOauthTokenGenerator, times(4)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCourtLogRequest(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            List<CourtLog> dartsApiCourtLogsResponse = someListOfCourtLog(3);
            courtLogsApi.returnsCourtLogs(dartsApiCourtLogsResponse);

            SoapAssertionUtil<GetCourtLogResponse> response = client.getCourtLogs(
                getGatewayUri(),
                getCourtLogs.getContentAsString(
                    Charset.defaultCharset())
            );

            com.synapps.moj.dfs.response.GetCourtLogResponse actualResponse = response.getResponse().getValue().getReturn();

            assertEquals("200", actualResponse.getCode());
            assertEquals("OK", actualResponse.getMessage());
            assertEquals(SOME_COURTHOUSE, actualResponse.getCourtLog().getCourthouse());
            assertEquals(SOME_CASE_NUMBER, actualResponse.getCourtLog().getCaseNumber());
            assertEquals("some-log-text-1", actualResponse.getCourtLog().getEntry().get(0).getValue());
            assertEquals("some-log-text-2", actualResponse.getCourtLog().getEntry().get(1).getValue());
            assertEquals("some-log-text-3", actualResponse.getCourtLog().getEntry().get(2).getValue());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        courtLogsApi.verifyReceivedGetCourtLogsRequestFor(SOME_COURTHOUSE, "some-case");

        WireMock.verify(getRequestedFor(urlMatching("/courtlogs.*"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    @SuppressWarnings("PMD.LawOfDemeter")
    void testRejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            courtLogsApi.returnsCourtLogs(someListOfCourtLog(1));

            org.assertj.core.api.Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
                Charset charset = Charset.defaultCharset();
                String invalidMessage = invalidSoapMessage.getContentAsString(charset);
                client.getCourtLogs(getGatewayUri(), invalidMessage);
            });
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        courtLogsApi.verifyDoesntReceiveRequest();

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testPostCourtLogsRoute(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            postCourtLogsApi.returnsEventResponse();
            SoapAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(
                getGatewayUri(),
                postCourtLogs.getContentAsString(Charset.defaultCharset())
            );

            postCourtLogsApi.verifyReceivedPostCourtLogsRequestForCaseNumber("CASE000001");

            assertEquals("200", response.getResponse().getValue().getReturn().getCode());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testPostCourtLogsRouteFailOnInvalidServiceResponse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            postCourtLogsApi.returnsFailureWhenAddingCourtLogs();

            SoapAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(
                getGatewayUri(),
                postCourtLogs.getContentAsString(Charset.defaultCharset())
            );
            SoapAssertionUtil.assertErrorResponse("404", "Courthouse Not Found",
                                                  response.getResponse().getValue().getReturn()
            );
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        postCourtLogsApi.verifyReceivedPostCourtLogsRequestForCaseNumber("CASE000001");

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testPostCourtLogsRejectsInvalidSoapMessage(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            postCourtLogsApi.returnsEventResponse();

            SoapAssertionUtil<AddLogEntryResponse> response = client.postCourtLogs(
                getGatewayUri(),
                invalidSoapMessage.getContentAsString(Charset.defaultCharset())
            );
            SoapAssertionUtil.assertErrorResponse("400", "Invalid XML Document",
                                                  response.getResponse().getValue().getReturn()
            );
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        postCourtLogsApi.verifyDoesntReceiveRequest();
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }


    private static List<CourtLog> someListOfCourtLog(int numberOfEntries) {
        return IntStream.rangeClosed(1, numberOfEntries)
            .mapToObj(CourtLogsWebServiceTest::courtLog)
            .collect(toList());
    }

    private static CourtLog courtLog(int index) {
        var courtLog = new CourtLog();
        courtLog.setCaseNumber(SOME_CASE_NUMBER);
        courtLog.setCourthouse(SOME_COURTHOUSE);
        courtLog.setTimestamp(OffsetDateTime.now().minusDays(index));
        courtLog.setEventText("some-log-text-" + index);
        return courtLog;
    }
}

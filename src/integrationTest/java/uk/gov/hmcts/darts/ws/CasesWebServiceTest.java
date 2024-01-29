package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;
import uk.gov.hmcts.darts.utils.AuthenticationAssertion;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token")
class CasesWebServiceTest extends IntegrationBase {

    @MockBean
    private TokenGenerator mockOauthTokenGenerator;

    @MockBean
    private TokenValidator tokenValidator;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("test");

        when(tokenValidator.validate(Mockito.eq("test"))).thenReturn(true);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");
            client.getCases(getGatewayUri(), soapRequestStr);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithIdentitiesFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
                "payloads/soapHeaderServiceContextNoIdentities.xml");
            client.setHeaderBlock(soapHeaderServiceContextStr);

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            client.getCases(getGatewayUri(), soapRequestStr);
            Assertions.fail("Never expect to get here");
        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            client.getCases(getGatewayUri(), soapRequestStr);
            Assertions.fail("Never expect to get here");
        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesWithAuthenticationToken(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithTokenHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/dartsApiResponse.json");

            stubFor(get(urlPathEqualTo("/cases"))
                        .willReturn(aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(dartsApiResponseStr)));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/expectedResponse.xml");

            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, GetCasesResponse.class).getValue());
        }, getContextClient(), getGatewayUri(), DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCases(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/dartsApiResponse.json");

            stubFor(get(urlPathEqualTo("/cases"))
                        .willReturn(aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(dartsApiResponseStr)));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/expectedResponse.xml");


            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, GetCasesResponse.class).getValue());
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verify(mockOauthTokenGenerator, times(1)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesServiceFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            getCasesApiStub.returnsFailureWhenGettingCases();

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            SoapAssertionUtil.assertErrorResponse("404", "Courthouse Not Found", response.getResponse().getValue().getReturn());
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCase(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/cases"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/expectedResponse.xml");


            SoapAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCaseError(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/invalidSoapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/cases")).willReturn(ok(dartsApiResponseStr)));

            Assertions.assertThrows(SoapFaultClientException.class, () -> {
                client.addCases(getGatewayUri(), soapRequestStr);
            });
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCaseWithInvalidServiceResponse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/cases"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/expectedResponse.xml");

            SoapAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCaseWithRedisNotStartedUnknownFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {

            String soapRequestStr = TestUtils.getContentsFromFile(
                    "payloads/addCase/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                    "payloads/addCase/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/cases"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            try {
                // stop redis to force an error
                stopRedis();
                client.addCases(getGatewayUri(), soapRequestStr);
                Assertions.fail();
            } catch (SoapFaultClientException e) {
                ServiceExceptionType type = AuthenticationAssertion.getSoapFaultDetails(e);
                Assertions.assertEquals(FaultErrorCodes.E_UNSUPPORTED_EXCEPTION, FaultErrorCodes.valueOf(type.getMessageId()));
                Assertions.assertEquals(
                        SoapFaultServiceException.getMessage(FaultErrorCodes.E_UNSUPPORTED_EXCEPTION.name()),
                        type.getMessage()
                );
            } finally {
                // start redis
                startRedis();
            }
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}

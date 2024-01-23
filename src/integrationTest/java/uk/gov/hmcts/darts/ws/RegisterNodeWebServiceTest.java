package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token")
class RegisterNodeWebServiceTest extends IntegrationBase {

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesRegisterNodeRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/registernode/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/register-devices"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/expectedResponse.xml");


            SoapAssertionUtil<RegisterNodeResponse> response = client.registerNode(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, RegisterNodeResponse.class).getValue());

        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routeRegisterNodeRequestWithIdentitiesFailure(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/registernode/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/register-devices"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/expectedResponse.xml");


            SoapAssertionUtil<RegisterNodeResponse> response = client.registerNode(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, RegisterNodeResponse.class).getValue());

        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesRegisterNodeRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/registernode/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/register-devices"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/expectedResponse.xml");


            SoapAssertionUtil<RegisterNodeResponse> response = client.registerNode(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, RegisterNodeResponse.class).getValue());

        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void handlesRegisterNodeWithAuthenticationToken(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithTokenHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/registernode/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/register-devices"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/expectedResponse.xml");


            SoapAssertionUtil<RegisterNodeResponse> response = client.registerNode(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, RegisterNodeResponse.class).getValue());
        },  getContextClient(), getGatewayUri(), DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesRegisterNode(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/registernode/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/register-devices"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/expectedResponse.xml");


            SoapAssertionUtil<RegisterNodeResponse> response = client.registerNode(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, RegisterNodeResponse.class).getValue());
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesRegisterNodeError(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/registernode/invalidSoapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/registernode/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/register-devices"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));


            Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
                client.registerNode(getGatewayUri(), soapRequestStr);
            });
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

}

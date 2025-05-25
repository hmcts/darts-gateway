package uk.gov.hmcts.darts.ws;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.authentication.component.SoapRequestInterceptor;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.testutils.IntegrationBase;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ActiveProfiles("int-test-jwt-token-shared")
class RegisterNodeWebServiceTest extends IntegrationBase {

    @MockitoBean
    private TokenGenerator mockOauthTokenGenerator;

    @BeforeEach
    public void before() {
        doReturn(DEFAULT_TOKEN).when(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        doReturn(DEFAULT_TOKEN).when(authSupport).getOrCreateValidToken(ContextRegistryParent.SERVICE_CONTEXT_USER, ContextRegistryParent.SERVICE_CONTEXT_USER);
        doNothing().when(authSupport).validateToken(DEFAULT_TOKEN);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesRegisterNodeRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {
        doThrow(new AuthenticationFailedException()).when(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

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

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        verify(authSupport, never()).getOrCreateValidToken(any(), any());
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
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(authSupport).validateToken(DEFAULT_TOKEN);
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

            // ensure that the payload logging is turned off for this api call
            org.junit.jupiter.api.Assertions.assertFalse(logAppender.searchLogs(SoapRequestInterceptor.REQUEST_PAYLOAD_PREFIX, null, null).isEmpty());

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        WireMock.verify(postRequestedFor(urlPathEqualTo("/register-devices"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));
        verify(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(authSupport).getOrCreateValidToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

}

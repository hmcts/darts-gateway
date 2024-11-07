package uk.gov.hmcts.darts.ws;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.authentication.component.SoapRequestInterceptor;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.common.client.exeption.AbstractClientProblemDecoder;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.testutils.IntegrationBase;

import java.io.IOException;
import java.util.List;
import javax.xml.transform.TransformerException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token-shared")
class CasesWebServiceTest extends IntegrationBase {

    @MockBean
    private TokenGenerator mockOauthTokenGenerator;

    @MockBean
    private TokenValidator tokenValidator;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("test");
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(ContextRegistryParent.SERVICE_CONTEXT_USER, ContextRegistryParent.SERVICE_CONTEXT_USER))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithAuthenticationFailure(DartsGatewayClient client) throws IOException, TransformerException, InterruptedException {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");
            client.getCases(getGatewayUri(), soapRequestStr);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithIdentitiesFailure(DartsGatewayClient client) throws IOException, TransformerException, InterruptedException {
        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
                "payloads/soapHeaderServiceContextNoIdentities.xml");
            client.setHeaderBlock(soapHeaderServiceContextStr);

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            client.getCases(getGatewayUri(), soapRequestStr);
            Assertions.fail("Never expect to get here");
        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesGetCasesRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws IOException, TransformerException, InterruptedException {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            client.getCases(getGatewayUri(), soapRequestStr);
            Assertions.fail("Never expect to get here");
        });

        verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesWithAuthenticationToken(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {

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
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesWithAuthenticationTokenWithRefresh(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {

        when(tokenValidator.test(Mockito.any(),
                                     Mockito.eq("downstreamtoken"))).thenReturn(true);

        // setup the tokens so that we refresh the backend token before making the restful darts calls
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("downstreamtoken", "test", "downstreamrefresh", "downstreamrefreshoutsidecache");

        authenticationStub.assertWithTokenHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile("payloads/getCases/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/dartsApiResponse.json");

            // assert the refreshed token is passed through to the darts api call
            stubFor(get(urlPathEqualTo("/cases"))
                        .willReturn(aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(dartsApiResponseStr)));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/expectedResponse.xml");

            when(tokenValidator.test(Mockito.any(),
                                         Mockito.eq("downstreamtoken"))).thenReturn(false);

            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, GetCasesResponse.class).getValue());
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        WireMock.verify(getRequestedFor(urlPathEqualTo("/cases"))
                            .withHeader("Authorization", new RegexPattern("Bearer downstreamrefreshoutsidecache")));

        verify(mockOauthTokenGenerator, times(4)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCases(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
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

            // ensure that the payload logging is turned off for this api call
            Assertions.assertFalse(logAppender.searchLogs(SoapRequestInterceptor.REQUEST_PAYLOAD_PREFIX, null, null).isEmpty());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
        WireMock.verify(getRequestedFor(urlPathEqualTo("/cases"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));

    }


    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesServiceFailure(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            getCasesApiStub.returnsFailureWhenGettingCases();

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            SoapAssertionUtil.assertErrorResponse("404", "Courthouse Not Found", response.getResponse().getValue().getReturn());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCase(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
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
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCaseError(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/invalidSoapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/cases")).willReturn(ok(dartsApiResponseStr)));

            Assertions.assertThrows(SoapFaultClientException.class, () -> {
                client.addCases(getGatewayUri(), soapRequestStr);
            });
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddCaseWithInvalidServiceResponse(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
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
            List<ServeEvent> allServeEvents = getAllServeEvents();
            ServeEvent request = allServeEvents.get(0);
            String body = request.getRequest().getBodyAsString();
            assertTrue(body.contains("\"case_type\":\"1\""));
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);


        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesFeignFailureWithNoProblem(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            stubFor(get(urlPathEqualTo("/cases"))
                        .willReturn(aResponse()
                                        .withStatus(500)));

            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            Assertions.assertEquals("500", response.getResponse().getValue().getReturn().getCode());
            Assertions.assertFalse(logAppender.searchLogs(AbstractClientProblemDecoder.RESPONSE_PREFIX
                                                              + "500 Server Error:", null, null).isEmpty());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
        WireMock.verify(getRequestedFor(urlPathEqualTo("/cases"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesGetCasesFeignFailureWithHtmlProblem(DartsGatewayClient client) throws IOException, JAXBException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

            stubFor(get(urlPathEqualTo("/cases"))
                        .willReturn(aResponse()
                                        .withStatus(500).withBody("<html><body>Internal Server Error</body></html>")));

            SoapAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
            Assertions.assertEquals("500", response.getResponse().getValue().getReturn().getCode());
            Assertions.assertFalse(logAppender.searchLogs(AbstractClientProblemDecoder.RESPONSE_PREFIX
                                                              + "500 Server Error:<html><body>Internal Server Error</body></html>", null, null).isEmpty());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(mockOauthTokenGenerator, times(2)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
        WireMock.verify(getRequestedFor(urlPathEqualTo("/cases"))
                            .withHeader("Authorization", new RegexPattern("Bearer test")));

    }
}
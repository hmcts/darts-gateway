package uk.gov.hmcts.darts.ws;

import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.service.mojdarts.synapps.com.AddAudioResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.component.impl.OauthTokenGenerator;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.utils.matcher.MultipartDartsProxyContentPattern;
import uk.gov.hmcts.darts.utils.multipart.DummyXmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
@ActiveProfiles("int-test-jwt-token-shared")
class AddAudioWebServiceTest extends IntegrationBase {

    @MockBean
    private XmlWithFileMultiPartRequestHolder requestHolder;

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @MockBean
    private TokenValidator validator;


    @Value("${darts-gateway.add-audio.fileSizeInMegaBytes}")
    private long maxByteSize;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("test");
        when(validator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        when(validator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(ContextRegistryParent.SERVICE_CONTEXT_USER, ContextRegistryParent.SERVICE_CONTEXT_USER))
            .thenReturn("test");
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesAddAudioRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            client.addAudio(getGatewayUri(), soapRequestStr);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);

        Mockito.verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesAddAudioRequestWithIdentitiesFailure(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            client.addAudio(getGatewayUri(), soapRequestStr);

        });

        Mockito.verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesAddAudioRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            client.addAudio(getGatewayUri(), soapRequestStr);
        });

        Mockito.verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioWithAuthenticationToken(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios"))
                       .withRequestBody(new MultipartDartsProxyContentPattern()));
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddAudioWithAuthenticationTokenWithRefresh(DartsGatewayClient client) throws Exception {

        when(validator.test(Mockito.any(),
                                     Mockito.eq("downstreamtoken"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("downstreamtoken", "test", "downstreamrefresh", "downstreamrefreshoutsidecache");

        authenticationStub.assertWithTokenHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            when(validator.test(Mockito.any(),
                                         Mockito.eq("downstreamtoken"))).thenReturn(false);

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios"))
                       .withRequestBody(new MultipartDartsProxyContentPattern()));
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);

        verify(postRequestedFor(urlPathEqualTo("/audios"))
                            .withHeader("Authorization", new RegexPattern("Bearer downstreamrefreshoutsidecache")));
        Mockito.verify(mockOauthTokenGenerator, times(4)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudio(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios"))
                       .withRequestBody(new MultipartDartsProxyContentPattern()));
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorFileSizeExceed(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
            when(request.getBinarySize()).thenReturn(AddAudioValidator.getBytes(maxByteSize) + 1);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            CodeAndMessage responseCode = CodeAndMessage.AUDIO_TOO_LARGE;
            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorInDocumentXml(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/invalidDocumentStructure.xml");

            XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
            when(request.getBinarySize()).thenReturn(maxByteSize);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            CodeAndMessage responseCode = CodeAndMessage.INVALID_XML;
            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioFailingResponse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(aResponse().withStatus(404).withBody("this is not a valid error format")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            CodeAndMessage responseCode = CodeAndMessage.ERROR;
            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
        verify(postRequestedFor(urlPathEqualTo("/audios"))
                   .withRequestBody(new MultipartDartsProxyContentPattern()));
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleEmptyCourthouse(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequestEmptyCourthouse.xml");

            XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
            when(request.getBinarySize()).thenReturn(maxByteSize);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            CodeAndMessage responseCode = CodeAndMessage.ERROR;
            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleUnknownCourthouse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequestUnknownCourthouse.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/problemResponse.json");

            stubFor(post(urlPathEqualTo("/audios"))
                        .willReturn(aResponse().withStatus(404)
                                        .withBody(dartsApiResponseStr)));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/problemResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios"))
                       .withRequestBody(new MultipartDartsProxyContentPattern()));
        }, DEFAULT_HEADER_USERNAME, DEFAULT_PASSWORD);
    }

    @Test
    void testAddAudioMtomNoXmlBody() throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestMTOMErrorNoXMLBody.txt");

        soapRequestStr = soapRequestStr.replace("${USER}", DEFAULT_HEADER_USERNAME);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", DEFAULT_PASSWORD);

        HttpRequest request = HttpRequest.newBuilder()
            .header("Content-Type", "multipart/related;boundary=\"uuid:b93ca7c1-d42d-4acd-aa56-3c9db058d44f\";")
            .uri(getGatewayUri().toURI())
            .POST(HttpRequest.BodyPublishers.ofString(soapRequestStr))
            .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(response.body().contains(FaultErrorCodes.E_UNSUPPORTED_EXCEPTION.name()));
    }

    @Test
    void testAddAudioMtomNoXmlPart() throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestMTOMFailureBadRequestNoXMLPart.txt");

        soapRequestStr = soapRequestStr.replace("${USER}", DEFAULT_HEADER_USERNAME);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", DEFAULT_PASSWORD);

        HttpRequest request = HttpRequest.newBuilder()
            .header("Content-Type", "multipart/related;boundary=\"uuid:b93ca7c1-d42d-4acd-aa56-3c9db058d44f\";")
            .uri(getGatewayUri().toURI())
            .POST(HttpRequest.BodyPublishers.ofString(soapRequestStr))
            .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }

    @Test
    void testAddAudioMtomNoXmlContent() throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestMTOMFailureBadRequestNoXMLContent.txt");

        soapRequestStr = soapRequestStr.replace("${USER}", DEFAULT_HEADER_USERNAME);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", DEFAULT_PASSWORD);

        HttpRequest request = HttpRequest.newBuilder()
            .header("Content-Type", "multipart/related;boundary=\"uuid:b93ca7c1-d42d-4acd-aa56-3c9db058d44f\";")
            .uri(getGatewayUri().toURI())
            .POST(HttpRequest.BodyPublishers.ofString(soapRequestStr))
            .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.body().isEmpty());
    }
}

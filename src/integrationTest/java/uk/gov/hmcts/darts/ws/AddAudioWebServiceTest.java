package uk.gov.hmcts.darts.ws;

import com.emc.documentum.fs.rt.ServiceException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.service.mojdarts.synapps.com.AddAudioResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.unit.DataSize;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.authentication.component.SoapRequestInterceptor;
import uk.gov.hmcts.darts.cache.AuthSupport;
import uk.gov.hmcts.darts.cache.token.component.impl.OauthTokenGenerator;
import uk.gov.hmcts.darts.common.client.exeption.AbstractClientProblemDecoder;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.datastore.DataManagementService;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.testutils.request.DummyXmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
@ActiveProfiles("int-test-jwt-token-shared")
class AddAudioWebServiceTest extends IntegrationBase {

    @MockitoBean
    private XmlWithFileMultiPartRequestHolder requestHolder;

    @MockitoBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @MockitoBean
    private AuthSupport authSupport;
    @MockitoBean
    private DataManagementService dataManagementService;

    @Value("${darts-gateway.add-audio.fileSizeInMegaBytes}")
    private DataSize maxByteSize;

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("test");
        //when(validator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        //when(validator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(ContextRegistryParent.SERVICE_CONTEXT_USER, ContextRegistryParent.SERVICE_CONTEXT_USER))
            .thenReturn("test");
        when(dataManagementService.saveBlobData(any(), any(), any())).thenReturn(uuid);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesAddAudioRequestWithAuthenticationFailure(DartsGatewayClient client) throws Exception {

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            client.addAudio(getGatewayUri(), soapRequestStr);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(mockOauthTokenGenerator).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void routesAddAudioRequestWithIdentitiesFailure(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            client.addAudio(getGatewayUri(), soapRequestStr);

        });

        Mockito.verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testRoutesAddAudioRequestWithAuthenticationTokenFailure(DartsGatewayClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            client.addAudio(getGatewayUri(), soapRequestStr);
        });

        Mockito.verify(mockOauthTokenGenerator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(mockOauthTokenGenerator);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioWithAuthenticationToken(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
                WireMock.matching(
                    "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                        "\"filename\":\"0001.a00\",\"courthouse\":\"SWANSEA\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\",\"file_size\":5854354," +
                        "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                        "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testHandlesAddAudioWithAuthenticationTokenWithRefresh(DartsGatewayClient client) throws Exception {

        //when(validator.test(any(), Mockito.eq("downstreamtoken"))).thenReturn(true);

        when(mockOauthTokenGenerator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("downstreamtoken", "test", "downstreamrefresh", "downstreamrefreshoutsidecache");

        authenticationStub.assertWithTokenHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            //    doNothing().when(validator).validateToken(Mockito.eq("downstreamtoken"));

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
                WireMock.matching(
                    "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                        "\"filename\":\"0001.a00\",\"courthouse\":\"SWANSEA\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\",\"file_size\":5854354," +
                        "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                        "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(postRequestedFor(urlPathEqualTo("/audios/metadata"))
                   .withHeader("Authorization", new RegexPattern("Bearer downstreamrefreshoutsidecache")));
        Mockito.verify(mockOauthTokenGenerator, times(4)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudio(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
            response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

            verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
                WireMock.matching(
                    "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                        "\"filename\":\"0001.a00\",\"courthouse\":\"SWANSEA\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\",\"file_size\":5854354," +
                        "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                        "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

            // ensure that the payload logging is turned off for this api call
            Assertions.assertFalse(logAppender.searchLogs(SoapRequestInterceptor.REQUEST_PAYLOAD_PREFIX, null, null).isEmpty());
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorFileSizeExceed(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            XmlWithFileMultiPartRequest request = spy(new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE));
            when(request.getBinarySize()).thenReturn(AddAudioValidator.getBytes(maxByteSize.toBytes()) + 1);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(
                TestUtils.getContentsFromFile("payloads/addAudio/register/dartsValidationExceptionAudioTooLargeResponse.xml"),
                ServiceException.class);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container", uuid);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorInDocumentXml(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/invalidDocumentStructure.xml");

            XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
            when(request.getBinarySize()).thenReturn(maxByteSize.toBytes());
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(
                TestUtils.getContentsFromFile("payloads/addAudio/register/dartsValidationExceptionInvalidXmlDocumentResponse.xml"),
                ServiceException.class);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioFailingResponse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(aResponse().withStatus(404).withBody("this is not a valid error format")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(TestUtils.getContentsFromFile(
                "payloads/addAudio/register/clientProblemException.xml"), ServiceException.class);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
            WireMock.matching(
                "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                    "\"filename\":\"0001.a00\",\"courthouse\":\"SWANSEA\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\",\"file_size\":5854354," +
                    "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                    "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

        Mockito.verify(dataManagementService).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleEmptyCourthouse(DartsGatewayClient client) throws Exception {

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequestEmptyCourthouse.xml");

            XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
            when(request.getBinarySize()).thenReturn(maxByteSize.toBytes());
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(TestUtils.getContentsFromFile("payloads/addAudio/register/dartsExceptionResponse.xml"),
                                                     ServiceException.class);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleUnknownCourthouse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequestUnknownCourthouse.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/problemResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(aResponse().withStatus(404)
                                        .withBody(dartsApiResponseStr)));

            String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/courtHouseNotFoundResponse.xml");

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(expectedResponseStr, ServiceException.class);

            verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
                WireMock.matching(
                    "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                        "\"filename\":\"0001.a00\",\"courthouse\":\"theunknowncourthouse\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\"" +
                        ",\"file_size\":5854354,\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                        "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService).deleteBlobData("darts-inbound-container",uuid);

    }

    @Test
    void testAddAudioMtomNoXmlBody() throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestMTOMErrorNoXMLBody.txt");

        soapRequestStr = soapRequestStr.replace("${USER}", DEFAULT_HEADER_USERNAME);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", DEFAULT_HEADER_PASSWORD);

        HttpRequest request = HttpRequest.newBuilder()
            .header("Content-Type", "multipart/related;boundary=\"uuid:b93ca7c1-d42d-4acd-aa56-3c9db058d44f\";")
            .uri(getGatewayUri().toURI())
            .POST(HttpRequest.BodyPublishers.ofString(soapRequestStr))
            .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response;

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertTrue(response.body().contains(FaultErrorCodes.E_UNKNOWN_CODE.name()));

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @Test
    void testAddAudioMtomNoXmlPart() throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestMTOMFailureBadRequestNoXMLPart.txt");

        soapRequestStr = soapRequestStr.replace("${USER}", DEFAULT_HEADER_USERNAME);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", DEFAULT_HEADER_PASSWORD);

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

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @Test
    void testAddAudioMtomNoXmlContent() throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestMTOMFailureBadRequestNoXMLContent.txt");

        soapRequestStr = soapRequestStr.replace("${USER}", DEFAULT_HEADER_USERNAME);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", DEFAULT_HEADER_PASSWORD);

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

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorMaxDurationExceed(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequestDurationExceeded.xml");

            XmlWithFileMultiPartRequest request = spy(new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE));
            when(request.getBinarySize()).thenReturn(AddAudioValidator.getBytes(maxByteSize.toBytes()));
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(
                TestUtils.getContentsFromFile("payloads/addAudio/register/dartsValidationExceptionAudioTooLargeResponse.xml"),
                ServiceException.class);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorInvalidExtension(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequestInvalidExtension.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/problemResponse500.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(aResponse().withStatus(500)
                                        .withBody(dartsApiResponseStr)));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(TestUtils.getContentsFromFile("payloads/addAudio/register/dartsValidationExceptionResponse.xml"),
                                                     ServiceException.class);

            Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container", uuid);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioHandleErrorInvalidSignature(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/problemResponse500.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(aResponse().withStatus(500)
                                        .withBody(dartsApiResponseStr)));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.BAD_SIGNATURE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(TestUtils.getContentsFromFile("payloads/addAudio/register/dartsValidationExceptionResponse.xml"),
                                                     ServiceException.class);

            Mockito.verify(dataManagementService, never()).deleteBlobData("darts-inbound-container",uuid);

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testAddAudioWithNoProblemEmptyResponse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withStatus(500)));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(TestUtils.getContentsFromFile("payloads/addAudio/register/clientProblemException.xml"),
                                                     ServiceException.class);

            Assertions.assertFalse(logAppender
                                       .searchLogs(
                                           AbstractClientProblemDecoder.RESPONSE_PREFIX +
                                               "500 Server Error on POST request for \"http://localhost:8090/audios/metadata\": \"{<EOL>" +
                                               "  \"code\": \"200\",<EOL>  \"message\": \"OK\"<EOL>}<EOL>\"", null, null).isEmpty());

            verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
                WireMock.matching(
                    "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                        "\"filename\":\"0001.a00\",\"courthouse\":\"SWANSEA\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\",\"file_size\":5854354," +
                        "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                        "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

            Mockito.verify(dataManagementService).deleteBlobData("darts-inbound-container", uuid);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudio_ThrowsInternalServerError_WithNoProblemHtmlResponse(DartsGatewayClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

            String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/dartsApiResponse.json");

            stubFor(post(urlPathEqualTo("/audios/metadata"))
                        .willReturn(ok(dartsApiResponseStr).withStatus(500).withBody("<html><body>Internal Server Error</body></html>")));

            XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
            when(requestHolder.getRequest()).thenReturn(Optional.of(request));

            SoapAssertionUtil<ServiceException> response = client.addAudioException(getGatewayUri(), soapRequestStr);
            response.assertIdenticalErrorResponseXml(TestUtils.getContentsFromFile("payloads/addAudio/register/clientProblemException.xml"),
                                                     ServiceException.class);
            Assertions.assertFalse(logAppender.searchLogs(AbstractClientProblemDecoder.RESPONSE_PREFIX
                                                              + "500 Server Error on POST request for \"http://localhost:8090/audios/metadata\": \"<html><body>Internal Server Error</body></html>\"", null, null).isEmpty());
            verify(postRequestedFor(urlPathEqualTo("/audios/metadata")).withRequestBody(
                WireMock.matching(
                    "\\{\"started_at\":1694082411.000000000,\"ended_at\":1694082589.000000000,\"channel\":1,\"total_channels\":4,\"format\":\"mpeg2\"," +
                        "\"filename\":\"0001.a00\",\"courthouse\":\"SWANSEA\",\"courtroom\":\"32\",\"media_file\":\"0001.a00\",\"file_size\":5854354," +
                        "\"checksum\":\"81ef8524d69c7ae6605baf37e425b574\",\"cases\":\\[\"T20230294\",\"U20230907-112949\"]," +
                        "\"storage_guid\":\"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\"}")));

            // ensure that the payload logging is turned off for this api call
            Assertions.assertFalse(logAppender.searchLogs(SoapRequestInterceptor.REQUEST_PAYLOAD_PREFIX, null, null).isEmpty());

            Mockito.verify(dataManagementService).deleteBlobData("darts-inbound-container",uuid);

        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

    }

}

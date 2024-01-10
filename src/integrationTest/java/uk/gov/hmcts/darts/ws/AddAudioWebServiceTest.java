package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddAudioResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.utils.matcher.MultipartDartsProxyContentPattern;
import uk.gov.hmcts.darts.utils.multipart.DummyXmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class AddAudioWebServiceTest extends IntegrationBase {

    @MockBean
    private XmlWithFileMultiPartRequestHolder requestHolder;

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

    @Value("${darts-gateway.add-audio.fileSizeInMegaBytes}")
    private long maxByteSize;

    @BeforeEach
    public void before() {
        when(mockOauthTokenGenerator.acquireNewToken("some-user", "some-password"))
            .thenReturn("test");
    }


    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudio(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

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
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleErrorFileSizeExceed(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

        XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
        when(request.getBinarySize()).thenReturn(AddAudioValidator.getBytes(maxByteSize) + 1);
        when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        CodeAndMessage responseCode = CodeAndMessage.AUDIO_TOO_LARGE;
        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleErrorInDocumentXml(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        final String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/invalidDocumentStructure.xml");

        XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
        when(request.getBinarySize()).thenReturn(maxByteSize);
        when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        CodeAndMessage responseCode = CodeAndMessage.INVALID_XML;
        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioFailingResponse(DartsGatewayClient client) throws Exception {

        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

        stubFor(post(urlPathEqualTo("/audios"))
                .willReturn(aResponse().withStatus(404).withBody("this is not a valid error format")));

        XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
        when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        CodeAndMessage responseCode = CodeAndMessage.ERROR;
        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());

        verify(postRequestedFor(urlPathEqualTo("/audios"))
                .withRequestBody(new MultipartDartsProxyContentPattern()));
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleEmptyCourthouse(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        final String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequestEmptyCourthouse.xml");

        XmlWithFileMultiPartRequest request = mock(XmlWithFileMultiPartRequest.class);
        when(request.getBinarySize()).thenReturn(maxByteSize);
        when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        CodeAndMessage responseCode = CodeAndMessage.ERROR;
        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleUnknownCourthouse(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

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
    }
}

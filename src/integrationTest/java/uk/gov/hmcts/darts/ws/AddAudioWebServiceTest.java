package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddAudioResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.darts.common.multipart.DummyXmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;


@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class AddAudioWebServiceTest  extends IntegrationBase {

    @MockBean
    private XmlWithFileMultiPartRequestHolder requestHolder;

    @Value("${darts-gateway.add-audio.fileSizeInBytes}")
    private long maxByteSize;

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudio(DartsGatewayClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/audios"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addAudio/register/expectedResponse.xml");

        XmlWithFileMultiPartRequest request = new DummyXmlWithFileMultiPartRequest(AddAudioMidTierCommand.SAMPLE_FILE);
        Mockito.when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleErrorFileSizeExceed(DartsGatewayClient client) throws Exception {
        final String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/audios"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        XmlWithFileMultiPartRequest request = Mockito.mock(XmlWithFileMultiPartRequest.class);
        Mockito.when(request.getBinarySize()).thenReturn(maxByteSize + 1);
        Mockito.when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        CodeAndMessage responseCode = CodeAndMessage.AUDIO_TOO_LARGE;
        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleErrorInDocumentXml(DartsGatewayClient client) throws Exception {
        final String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/invalidDocumentStructure.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/audios"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        XmlWithFileMultiPartRequest request = Mockito.mock(XmlWithFileMultiPartRequest.class);
        Mockito.when(request.getBinarySize()).thenReturn(maxByteSize);
        Mockito.when(requestHolder.getRequest()).thenReturn(Optional.of(request));

        CodeAndMessage responseCode = CodeAndMessage.INVALID_XML;
        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        Assertions.assertEquals(responseCode.getCode(), response.getResponse().getValue().getReturn().getCode());
    }
}

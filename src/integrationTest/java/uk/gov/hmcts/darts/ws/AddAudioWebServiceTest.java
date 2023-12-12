package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddAudioResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ActiveProfiles("int-test-jwt-token")
class AddAudioWebServiceTest extends IntegrationBase {

    @MockBean
    private OauthTokenGenerator mockOauthTokenGenerator;

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

        SoapAssertionUtil<AddAudioResponse> response = client.addAudio(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddAudioResponse.class).getValue());

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void addAudioHandleError(DartsGatewayClient client) throws Exception {
        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");
        client.setHeaderBlock(soapHeaderServiceContextStr);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addAudio/register/invalidSoapRequest.xml");

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.addAudio(getGatewayUri(), soapRequestStr);
        });

        verify(mockOauthTokenGenerator).acquireNewToken("some-user", "some-password");
        verifyNoMoreInteractions(mockOauthTokenGenerator);
    }

}

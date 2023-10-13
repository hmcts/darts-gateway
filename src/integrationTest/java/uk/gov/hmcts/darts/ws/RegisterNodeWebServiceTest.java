package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.RegisterNodeResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.ClientProvider;
import uk.gov.hmcts.darts.utils.client.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.client.DartsGatewayClient;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class RegisterNodeWebServiceTest extends IntegrationBase {

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesRegisterNode(DartsGatewayClient client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/registernode/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/registernode/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/register-devices"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));

        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/registernode/expectedResponse.xml");


        DartsGatewayAssertionUtil<RegisterNodeResponse> response = client.registerNode(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, RegisterNodeResponse.class).getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesRegisterNodeError(DartsGatewayClient client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/registernode/invalidSoapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/registernode/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/register-devices"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));


        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.registerNode(getGatewayUri(), soapRequestStr);
        });
    }

}

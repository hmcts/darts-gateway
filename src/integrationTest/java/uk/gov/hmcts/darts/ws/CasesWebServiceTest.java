package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.SOAPAssertionUtil;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class CasesWebServiceTest extends IntegrationBase {

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void handlesGetCases(DartsGatewayClient client) throws Exception {
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

        SOAPAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, GetCasesResponse.class).getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void handlesGetCasesServiceFailure(DartsGatewayClient client) throws Exception {
        getCasesApiStub.returnsFailureWhenGettingCases();

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

        SOAPAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayUri(), soapRequestStr);
        SOAPAssertionUtil.assertErrorResponse("404", "Courthouse Not Found", response.getResponse().getValue().getReturn());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void handlesAddCase(DartsGatewayClient client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/cases"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/expectedResponse.xml");


        SOAPAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void handlesAddCaseError(DartsGatewayClient client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/invalidSoapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/cases")).willReturn(ok(dartsApiResponseStr)));

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(() -> {
            client.addCases(getGatewayUri(), soapRequestStr);
        });
    }

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void handlesAddCaseWithInvalidServiceResponse(DartsGatewayClient client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/expectedResponse.xml");

        SOAPAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayUri(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
    }
}

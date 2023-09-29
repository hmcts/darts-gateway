package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.ClientProvider;
import uk.gov.hmcts.darts.utils.client.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.client.DartsGatewayClientable;
import uk.gov.hmcts.darts.utils.client.DartsGatewayMTOMClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class CasesWebServiceTest extends IntegrationBase {

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesGetCases(DartsGatewayClientable client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/dartsApiResponse.json");


        stubFor(get(urlPathEqualTo("/cases"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(dartsApiResponseStr)));

        String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/getCases/expectedResponse.xml");

        DartsGatewayAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayURI(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, GetCasesResponse.class).getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesGetCasesServiceFailure(DartsGatewayClientable client) throws Exception {
        getCasesApiStub.returnsFailureWhenGettingCases();

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

        DartsGatewayAssertionUtil<GetCasesResponse> response = client.getCases(getGatewayURI(), soapRequestStr);
        DartsGatewayAssertionUtil.assertErrorResponse("404", "Courthouse Not Found", response.getResponse().getValue().getReturn());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesAddCase(DartsGatewayClientable client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/expectedResponse.xml");


        DartsGatewayAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayURI(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesAddCaseError(DartsGatewayClientable client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/invalidSoapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/cases")).willReturn(ok(dartsApiResponseStr)));

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(()->
        {
            DartsGatewayAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayURI(), soapRequestStr);
        });

    }

    @ParameterizedTest
    @ArgumentsSource(ClientProvider.class)
    void handlesAddCaseWithInvalidServiceResponse(DartsGatewayClientable client) throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/expectedResponse.xml");

        DartsGatewayAssertionUtil<AddCaseResponse> response = client.addCases(getGatewayURI(), soapRequestStr);
        response.assertIdenticalResponse(client.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
    }
}

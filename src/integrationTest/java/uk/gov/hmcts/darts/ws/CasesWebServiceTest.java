package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.xml.transform.StringSource;
import org.xmlunit.matchers.CompareMatcher;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.motm.DartsGatewayAssertionUtil;
import uk.gov.hmcts.darts.utils.motm.DartsGatewayMTOMClient;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class CasesWebServiceTest extends IntegrationBase {
    @Autowired
    private DartsGatewayMTOMClient motmClient;

    @Test
    void handlesGetCases() throws Exception {
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

        DartsGatewayAssertionUtil<GetCasesResponse> response = motmClient.getCases(getGatewayURI(), soapRequestStr);
        response.assertIdenticalResponse(motmClient.convertData(expectedResponseStr, GetCasesResponse.class).getValue());
    }

    @Test
    void handlesGetCasesServiceFailure() throws Exception {
        getCasesApiStub.returnsFailureWhenGettingCases();

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

        DartsGatewayAssertionUtil<GetCasesResponse> response = motmClient.getCases(getGatewayURI(), soapRequestStr);
        DartsGatewayAssertionUtil.assertErrorResponse("404", "Courthouse Not Found", response.getResponse().getValue().getReturn());
    }

    @Test
    void handlesAddCase() throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/expectedResponse.xml");


        DartsGatewayAssertionUtil<AddCaseResponse> response = motmClient.addCases(getGatewayURI(), soapRequestStr);
        response.assertIdenticalResponse(motmClient.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
    }

    @Test
    void handlesAddCaseError() throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/invalidSoapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/cases")).willReturn(ok(dartsApiResponseStr)));

        Assertions.assertThatExceptionOfType(SoapFaultClientException.class).isThrownBy(()->
        {
            DartsGatewayAssertionUtil<AddCaseResponse> response = motmClient.addCases(getGatewayURI(), soapRequestStr);
        });

    }

    @Test
    void handlesAddCaseWithInvalidServiceResponse() throws Exception {

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/expectedResponse.xml");

        DartsGatewayAssertionUtil<AddCaseResponse> response = motmClient.addCases(getGatewayURI(), soapRequestStr);
        response.assertIdenticalResponse(motmClient.convertData(expectedResponseStr, AddCaseResponse.class).getValue());
    }
}

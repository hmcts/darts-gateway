package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.xml.transform.StringSource;
import org.xmlunit.matchers.CompareMatcher;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;

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
    private MockWebServiceClient wsClient;

    @Test
    void handlesGetCases() throws IOException {
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
        ResponseActions responseActions = wsClient.sendRequest(withPayload(soapRequest))
                .andExpect(noFault());
        String actualResponse = TestUtils.getResponse(responseActions);
        assertThat(actualResponse, CompareMatcher.isIdenticalTo(expectedResponseStr));
    }

    @Test
    void handlesGetCasesServiceFailure() throws IOException {
        getCasesApiStub.returnsFailureWhenGettingCases();

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/getCases/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);

        wsClient.sendRequest(withPayload(soapRequest))
                .andExpect(noFault()).andExpect(noFault())
                .andExpect(xpath("//code").evaluatesTo("404"))
                .andExpect(xpath("//message").evaluatesTo("Courthouse Not Found"));
    }

    @Test
    void handlesAddCase() throws IOException {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                    .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/expectedResponse.xml");

        ResponseActions responseActions = wsClient.sendRequest(withPayload(soapRequest))
            .andExpect(noFault());
        String actualResponse = TestUtils.getResponse(responseActions);
        assertThat(actualResponse, CompareMatcher.isSimilarTo(expectedResponseStr).ignoreWhitespace());
    }

    @Test
    void handlesAddCaseError() throws IOException {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/addCase/invalidSoapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/addCase/dartsApiResponse.json");

        stubFor(post(urlPathEqualTo("/cases")).willReturn(ok(dartsApiResponseStr)));

        wsClient.sendRequest(withPayload(soapRequest))
            .andExpect(clientOrSenderFault());
    }

    @Test
    void handlesAddCaseWithInvalidServiceResponse() throws IOException {

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                .willReturn(ok(dartsApiResponseStr).withHeader("Content-Type", "application/json")));
        String expectedResponseStr = TestUtils.getContentsFromFile(
                "payloads/addCase/expectedResponse.xml");

        ResponseActions responseActions = wsClient.sendRequest(withPayload(soapRequest))
                .andExpect(noFault());
        String actualResponse = TestUtils.getResponse(responseActions);
        assertThat(actualResponse, CompareMatcher.isSimilarTo(expectedResponseStr).ignoreWhitespace());
    }
}

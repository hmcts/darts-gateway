package uk.gov.hmcts.darts.ws;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class CasesWebServiceTest extends IntegrationBase {
    @Autowired
    private MockWebServiceClient wsClient;

    @Test
    void handlesGetCases() throws IOException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "tests/cases/CasesApiTest/handlesGetCases/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "tests/cases/CasesApiTest/handlesGetCases/dartsApiResponse.json");


        stubFor(get(urlPathEqualTo("/cases"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(dartsApiResponseStr)));

        String expectedResponseStr = TestUtils.getContentsFromFile(
            "tests/cases/CasesApiTest/handlesGetCases/expectedResponse.xml");
        StringSource expectedResponse = new StringSource(expectedResponseStr);

        ResponseActions responseActions = wsClient.sendRequest(withPayload(soapRequest))
            .andExpect(noFault());
        responseActions.andExpect(payload(expectedResponse));
    }

    @Test
    void handlesAddCase() throws IOException {

        String soapRequestStr = TestUtils.getContentsFromFile(
            "tests/cases/CasesApiTest/addCase/soapRequest.xml");

        StringSource soapRequest = new StringSource(soapRequestStr);
        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "tests/cases/CasesApiTest/addCase/dartsApiResponse.json");


        stubFor(post(urlPathEqualTo("/cases"))
                    .willReturn(ok(dartsApiResponseStr)));
        String expectedResponseStr = TestUtils.getContentsFromFile(
            "tests/cases/CasesApiTest/addCase/expectedResponse.xml");
        StringSource expectedResponse = new StringSource(expectedResponseStr);

        ResponseActions responseActions = wsClient.sendRequest(withPayload(soapRequest))
            .andExpect(noFault());
        responseActions.andExpect(payload(expectedResponse));
    }
}

package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseActions;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.utils.TestUtils;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

@WebServiceServerTest
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
@AutoConfigureWireMock(port = 8090)
class CasesApiTest {
    @Autowired
    MockWebServiceClient wsClient;

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
}

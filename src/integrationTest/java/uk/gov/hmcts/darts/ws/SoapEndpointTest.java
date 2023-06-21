package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

@WebServiceServerTest
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan("uk.gov.hmcts.darts")
@ActiveProfiles("int-test")
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
@AutoConfigureWireMock(port = 8090)
class SoapEndpointTest {

    @Autowired
    MockWebServiceClient wsClient;

    @Value("classpath:payloads/validEvent.xml")
    Resource validEvent;

    @Test
    void handlesEventPayload() throws IOException {

        stubFor(post(urlEqualTo("/events"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("""
                                                  {
                                                      "code": "200",
                                                      "message": "OK"
                                                  }""")));

        StringSource expectedResponse = new StringSource(
            """
                <ns3:addDocumentResponse xmlns:ns3="http://com.synapps.mojdarts.service.com">
                    <return>
                        <code>200</code>
                        <message>OK</message>
                    </return>
                </ns3:addDocumentResponse>"""
        );

        wsClient.sendRequest(withPayload(validEvent))
            .andExpect(noFault())
            .andExpect(payload(expectedResponse));
    }
}

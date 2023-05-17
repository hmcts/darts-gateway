package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import java.io.IOException;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;

@WebServiceServerTest
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class SoapEndpointTest {

    @Autowired
    MockWebServiceClient client;

    @Value("classpath:payloads/addCase.xml")
    Resource addCaseResource;

    @Value("classpath:payloads/freeTextEvent.xml")
    Resource freeTextEventResource;

    @Test
    void handlesEventPayload() throws IOException {
        StringSource expectedResponse = new StringSource(
            "<ns3:addDocumentResponse xmlns:ns3=\"http://com.synapps.mojdarts.service.com\"><return><code>500</code></return></ns3:addDocumentResponse>"
        );

        client.sendRequest(withPayload(freeTextEventResource))
            .andExpect(noFault())
            .andExpect(payload(expectedResponse));
    }

    @Test
    void handlesAddCasePayload() throws IOException {
        StringSource expectedResponse = new StringSource(
            "<ns3:addCaseResponse xmlns:ns3=\"http://com.synapps.mojdarts.service.com\"><return><code>500</code></return></ns3:addCaseResponse>"
        );

        client.sendRequest(withPayload(addCaseResource))
            .andExpect(noFault())
            .andExpect(payload(expectedResponse));
    }
}

package uk.gov.hmcts.darts.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.darts.utils.DarPcStub;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWireMock(port = 8090)
@SpringBootTest
@ActiveProfiles({"int-test"})
@AutoConfigureMockMvc
class DarNotifyControllerTest {

    private static final String VALID_NOTIFICATION_JSON = """
        {
          "notification_url": "http://localhost:8090/VIQDARNotifyEvent/DARNotifyEvent.asmx",
          "notification_type": "3",
          "timestamp": "2024-04-25T14:20:40.637Z",
          "courthouse": "York",
          "courtroom": "1",
          "case_numbers": [
            "T20240000"
          ]
        }
        """;
    private static final String EXPECTED_DAR_PC_NOTIFICATION = """
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
          <SOAP-ENV:Header/>
          <SOAP-ENV:Body>
            <DARNotifyEvent xmlns="http://www.VIQSoultions.com">
              <XMLEventDocument>&lt;Event type="3" Y="2024" M="4" D="25" H="15" MIN="20" S="40"&gt;&lt;courthouse&gt;York&lt;/courthouse&gt;&lt;courtroom&gt;1&lt;/courtroom&gt;&lt;case_numbers&gt;&lt;case_number&gt;T20240000&lt;/case_number&gt;&lt;/case_numbers&gt;&lt;/Event&gt;</XMLEventDocument>
            </DARNotifyEvent>
          </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
        """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DarPcStub darPcStub;


    @Test
    void shouldSendDarNotifyEventSoapAction() throws Exception {
        darPcStub.respondWithSuccessResponse();

        mockMvc.perform(post("/events/dar-notify")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(VALID_NOTIFICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        darPcStub.verifyNotificationReceivedWithBody(EXPECTED_DAR_PC_NOTIFICATION);
    }

    @Test
    void shouldHandleDarNotifyMalformedErrorResponse() throws Exception {
        darPcStub.respondWithMalformedResponse();

        mockMvc.perform(post("/events/dar-notify")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(VALID_NOTIFICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        darPcStub.verifyNotificationReceivedWithBody(EXPECTED_DAR_PC_NOTIFICATION);
    }

}

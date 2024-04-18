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
          "timestamp": "2023-06-19T14:52:40.637Z",
          "courthouse": "Test Court",
          "courtroom": "1",
          "case_numbers": [
            "A123456"
          ]
        }
        """;
    private static final String EXPECTED_DAR_PC_NOTIFICATION = """
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
          <SOAP-ENV:Header/>
          <SOAP-ENV:Body>
            <ns3:DARNotifyEvent xmlns:ns3="http://www.VIQSoultions.com">
              <XMLEventDocument>
                <event D="19" H="15" M="6" MIN="52" S="40" Y="2023" type="3">
                  <courthouse>Test Court</courthouse>
                  <courtroom>1</courtroom>
                  <case_numbers>
                    <case_number>A123456</case_number>
                  </case_numbers>
                </event>
              </XMLEventDocument>
            </ns3:DARNotifyEvent>
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

}

package uk.gov.hmcts.darts.event.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.darts.utils.DarPcStub;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWireMock(port = 8090)
@SpringBootTest
@ActiveProfiles({"int-test"})
@AutoConfigureMockMvc
@TestPropertySource(properties = "darts-gateway.events.dar-notify-event.enabled=false")
class DisablingDarPcNotificationTest {

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DarPcStub darPcStub;


    @Test
    void shouldNotSendDarNotifyEventWhenNotificationsDisabled() throws Exception {
        mockMvc.perform(post("/events/dar-notify")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(VALID_NOTIFICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        darPcStub.verifyNoNotificationReceived();
    }

}

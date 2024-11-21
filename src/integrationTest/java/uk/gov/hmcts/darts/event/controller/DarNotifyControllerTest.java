package uk.gov.hmcts.darts.event.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.darts.conf.ServiceTestConfiguration;
import uk.gov.hmcts.darts.testutils.stub.DarPcStub;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureWireMock(port = 8090)
@SpringBootTest(classes = ServiceTestConfiguration.class)
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
              <XMLEventDocument>&lt;event type="3" Y="2024" M="4" D="25" H="15" MIN="20" S="40"&gt;&lt;courthouse&gt;York&lt;/courthouse&gt;&lt;courtroom&gt;1&lt;/courtroom&gt;&lt;case_numbers&gt;&lt;case_number&gt;T20240000&lt;/case_number&gt;&lt;/case_numbers&gt;&lt;/event&gt;</XMLEventDocument>
            </DARNotifyEvent>
          </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
        """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DarPcStub darPcStub;
    @Autowired
    private Clock clock;

    @BeforeEach
    void setup() {
        darPcStub.reset();
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldSendDarNotifyEventSoapAction(CapturedOutput capturedOutput) throws Exception {
        darPcStub.respondWithSuccessResponse(OffsetDateTime.now(clock));

        mockMvc.perform(post("/events/dar-notify")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(VALID_NOTIFICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        darPcStub.verifyNotificationReceivedWithBody(EXPECTED_DAR_PC_NOTIFICATION);
        assertThat(capturedOutput)
            .doesNotContain("Response time from DAR PC is outside max drift limits of");
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldSendDarNotifyEventSoapActionDarPcDateOutSideDriftLimitsRangeBehind(CapturedOutput capturedOutput) throws Exception {
        OffsetDateTime responseDateTime = OffsetDateTime.now(clock).minusSeconds(90).truncatedTo(ChronoUnit.SECONDS);
        darPcStub.respondWithSuccessResponse(responseDateTime);

        mockMvc.perform(post("/events/dar-notify")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(VALID_NOTIFICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        darPcStub.verifyNotificationReceivedWithBody(EXPECTED_DAR_PC_NOTIFICATION);
        assertThat(capturedOutput)
            .containsPattern("Response time from DAR PC is outside max drift limits of 1 minute 30 seconds. DAR PC Response time: "
                                 + responseDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
                                 + ", Current time: [0-9\\-.T:]+Z for courthouse: York in courtroom: 1");
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void shouldSendDarNotifyEventSoapActionDarPcDateOutSideDriftLimitsRangeAhead(CapturedOutput capturedOutput) throws Exception {
        OffsetDateTime responseDateTime = OffsetDateTime.now(clock).plusMinutes(2).truncatedTo(ChronoUnit.SECONDS);
        darPcStub.respondWithSuccessResponse(responseDateTime);

        mockMvc.perform(post("/events/dar-notify")
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(VALID_NOTIFICATION_JSON))
            .andExpect(status().is2xxSuccessful());

        darPcStub.verifyNotificationReceivedWithBody(EXPECTED_DAR_PC_NOTIFICATION);
        assertThat(capturedOutput)
            .containsPattern("Response time from DAR PC is outside max drift limits of 1 minute 30 seconds. DAR PC Response time: "
                                 + responseDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
                                 + ", Current time: [0-9\\-.T:]+Z for courthouse: York in courtroom: 1");
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

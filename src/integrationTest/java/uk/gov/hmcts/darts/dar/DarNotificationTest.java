package uk.gov.hmcts.darts.dar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.darts.event.model.DarNotifyEvent;
import uk.gov.hmcts.darts.event.service.DarNotifyEventService;
import uk.gov.hmcts.darts.utils.IntegrationBase;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class DarNotificationTest extends IntegrationBase {

    @Autowired
    private DarNotifyEventService darNotifyEventService;

    @Test
    void sendsDarNotification() throws IOException {
        stubFor(post(urlPathEqualTo("/VIQDARNotifyEvent/DARNotifyEvent.asmx"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "text/xml; charset=utf-8")
                                    .withHeader("Content-Disposition", "inline;filename=f.txt")
                                    .withHeader("Content-length", "100")
                                    .withBody("<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><DARNotifyEventResponse xmlns=\"http://www.VIQSoultions.com\"><DARNotifyEventResult>0</DARNotifyEventResult></DARNotifyEventResponse></soap:Body></soap:Envelope>")));

        DarNotifyEvent notifyEvent = DarNotifyEvent.builder()
            .notificationUrl("http://localhost:8090/VIQDARNotifyEvent/DARNotifyEvent.asmx")
            .caseNumbers(Arrays.asList("C001"))
            .courthouse("LEEDS")
            .courtroom("1")
            .notificationType("3")
            .timestamp(OffsetDateTime.now())
            .build();

        darNotifyEventService.darNotify(notifyEvent);
    }


}

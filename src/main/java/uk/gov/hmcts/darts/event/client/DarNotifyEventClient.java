package uk.gov.hmcts.darts.event.client;

import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.DARNotifyEventResponse;
import com.service.viq.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.hmcts.darts.event.enums.DarNotifyEventResult;
import uk.gov.hmcts.darts.log.api.LogApi;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static java.lang.Integer.parseInt;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.WARN;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.OK;

@Component
@Slf4j
@RequiredArgsConstructor
public class DarNotifyEventClient {

    private final String soapAction;
    private final WebServiceTemplate webServiceTemplate;
    private final LogApi logApi;

    // This SOAP Web Service operation (DARNotifyEvent) still needs to be fully integration tested
    public boolean darNotifyEvent(String uri, DARNotifyEvent request, Event event) {
        boolean successful = false;

        var caseNumber = event.getCaseNumbers().getCaseNumber().toString();

        try {
            log.info("Sending notification to to DAR PC with case number: {}", caseNumber);
            Object responseObj = webServiceTemplate.marshalSendAndReceive(uri, request, new SoapActionCallback(soapAction));
            if (responseObj instanceof DARNotifyEventResponse response) {
                var result = DarNotifyEventResult.findByResult(response.getDARNotifyEventResult());

                if (OK.equals(result)) {
                    logApi.notificationSucceeded(uri, event.getCourthouse(), event.getCourtroom(), caseNumber, dateTimeFrom(event),
                         response.getDARNotifyEventResult());
                    successful = true;
                } else if (result != null) {
                    logApi.notificationFailedWithCode(
                        uri,
                        event.getCourthouse(),
                        event.getCourtroom(),
                        caseNumber,
                        dateTimeFrom(event),
                        "FAILED",
                        result.getMessage(),
                        response.getDARNotifyEventResult(),
                        WARN
                    );
                } else {
                    logApi.notificationFailedWithCode(
                        uri,
                        event.getCourthouse(),
                        event.getCourtroom(),
                        caseNumber,
                        dateTimeFrom(event),
                        "FAILED",
                        "result code not recognised",
                        response.getDARNotifyEventResult(),
                        WARN
                    );
                }
            } else {
                logApi.notificationFailed(
                    uri, event.getCourthouse(), event.getCourtroom(), caseNumber, dateTimeFrom(event), "FAILED", "No response", WARN);
            }

        } catch (WebServiceException webServiceException) {
            logApi.notificationFailed(
                uri, event.getCourthouse(), event.getCourtroom(), caseNumber, dateTimeFrom(event), "FAILED",
                "WebServiceException thrown. Failed to send", ERROR);
        }

        return successful;
    }

    private static OffsetDateTime dateTimeFrom(Event event) {
        return OffsetDateTime.of(
            parseInt(event.getY()),
            parseInt(event.getM()),
            parseInt(event.getD()),
            parseInt(event.getH()),
            parseInt(event.getMIN()),
            parseInt(event.getS()),
            0,
            ZoneOffset.UTC
        );
    }

}

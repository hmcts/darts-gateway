package uk.gov.hmcts.darts.event.client;

import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.DARNotifyEventResponse;
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
    public boolean darNotifyEvent(String uri, DARNotifyEvent request) {
        boolean successful = false;

        var caseNumber = request.getXMLEventDocument().getEvent().getCaseNumbers().getCaseNumber().toString();
        var event = request.getXMLEventDocument().getEvent();

        try {
            Object responseObj = webServiceTemplate.marshalSendAndReceive(uri, request, new SoapActionCallback(soapAction));
            if (responseObj instanceof DARNotifyEventResponse response) {
                var result = DarNotifyEventResult.findByResult(response.getDARNotifyEventResult());

                if (OK.equals(result)) {
                    logApi.notificationSucceeded(uri, event.getCourthouse(), event.getCourtroom(), caseNumber, dateTimeFrom(request));
                    successful = true;
                } else if (result != null) {
                    logApi.notificationFailed(
                        uri,
                        event.getCourthouse(),
                        event.getCourtroom(),
                        caseNumber,
                        dateTimeFrom(request),
                        "FAILED",
                        result.getMessage(),
                        WARN
                    );
                } else {
                    logApi.notificationFailedWithCode(
                        uri,
                        event.getCourthouse(),
                        event.getCourtroom(),
                        caseNumber,
                        dateTimeFrom(request),
                        "FAILED",
                        "result code not recognised",
                        response.getDARNotifyEventResult(),
                        WARN
                    );
                }
            } else {
                logApi.notificationFailed(
                    uri, event.getCourthouse(), event.getCourtroom(), caseNumber, dateTimeFrom(request), "FAILED", "No response", WARN);
            }

        } catch (WebServiceException webServiceException) {
            logApi.notificationFailed(
                uri, event.getCourthouse(), event.getCourtroom(), caseNumber, dateTimeFrom(request), "FAILED", "fail to send", ERROR);
        }

        return successful;
    }

    private static OffsetDateTime dateTimeFrom(DARNotifyEvent darNotifyEvent) {
        var eventDetails = darNotifyEvent.getXMLEventDocument().getEvent();
        return OffsetDateTime.of(
            parseInt(eventDetails.getY()),
            parseInt(eventDetails.getM()),
            parseInt(eventDetails.getD()),
            parseInt(eventDetails.getH()),
            parseInt(eventDetails.getMIN()),
            parseInt(eventDetails.getS()),
            0,
            ZoneOffset.UTC
        );
    }

}

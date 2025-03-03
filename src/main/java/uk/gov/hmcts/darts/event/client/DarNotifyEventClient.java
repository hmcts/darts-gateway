package uk.gov.hmcts.darts.event.client;

import com.service.viq.event.Event;
import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.DARNotifyEventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpComponentsConnection;
import uk.gov.hmcts.darts.event.enums.DarNotifyEventResult;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.utilities.XmlParser;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

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
    private final DarPcTimeLogInterceptor darPcTimeLogInterceptor;

    @PostConstruct
    void postConstruct() {
        webServiceTemplate.setInterceptors(new ClientInterceptor[]{darPcTimeLogInterceptor});
    }

    // This SOAP Web Service operation (DARNotifyEvent) still needs to be fully integration tested
    public boolean darNotifyEvent(String uri, DARNotifyEvent request, Event event) {
        boolean successful = false;
        String caseNumber = event.getCaseNumbers().getCaseNumber().toString();

        try {
            log.info("Sending notification to DAR PC with case number: {}", caseNumber);
            Object responseObj = webServiceTemplate.marshalSendAndReceive(uri, request, new SoapActionCallback(soapAction));
            if (responseObj instanceof DARNotifyEventResponse response) {
                DarNotifyEventResult result = DarNotifyEventResult.findByResult(response.getDARNotifyEventResult());

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

    @Component
    @RequiredArgsConstructor
    static class DarPcTimeLogInterceptor implements ClientInterceptor {

        private final Clock clock;

        @Value("${darts-gateway.dar-pc-max-time-drift}")
        private Duration maxTimeDrift;

        @Override
        public boolean handleRequest(MessageContext messageContext) {
            return true;
        }

        @Override
        public boolean handleResponse(MessageContext messageContext) {
            return true;
        }

        @Override
        public boolean handleFault(MessageContext messageContext) {
            return true;
        }

        @Override
        public void afterCompletion(MessageContext messageContext, Exception ex) {
            try {
                HttpComponentsConnection connection = (HttpComponentsConnection) TransportContextHolder.getTransportContext().getConnection();
                if (connection == null || connection.getHttpResponse() == null) {
                    return;
                }

                Header dateHeader = connection.getHttpResponse().getFirstHeader("Date");
                if (dateHeader == null) {
                    log.info("Date header not found in DAR response");
                    return;
                }
                OffsetDateTime responseDateTime = OffsetDateTime.parse(dateHeader.getValue(), DateTimeFormatter.RFC_1123_DATE_TIME);
                OffsetDateTime currentTime = OffsetDateTime.now(clock);

                if (currentTime.minus(maxTimeDrift).isAfter(responseDateTime) || currentTime.plus(maxTimeDrift).isBefore(responseDateTime)) {
                    SoapMessage message = (SoapMessage) messageContext.getRequest();
                    SoapBody soapBody = message.getSoapBody();
                    Source bodySource = soapBody.getPayloadSource();
                    DOMSource bodyDomSource = (DOMSource) bodySource;

                    DARNotifyEvent darNotifyEvent = XmlParser.unmarshal(bodyDomSource, DARNotifyEvent.class);
                    Event event = XmlParser.unmarshal(darNotifyEvent.getXMLEventDocument(), Event.class);

                    log.warn("Response time from DAR PC is outside max drift limits of {}. " +
                                 "DAR PC Response time: '{}', Current time: '{}' for courthouse: {} in courtroom: {}",
                             DurationFormatUtils.formatDurationWords(maxTimeDrift.toMillis(), true, true),
                             dateHeader.getValue(),
                             currentTime.format(DateTimeFormatter.RFC_1123_DATE_TIME),
                             event.getCourthouse(),
                             event.getCourtroom());
                }
            } catch (Exception e) {
                log.error("Error in DarPcTimeLogInterceptor", e);
            }
        }
    }
}

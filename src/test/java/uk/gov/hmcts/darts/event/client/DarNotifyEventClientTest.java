package uk.gov.hmcts.darts.event.client;

import com.service.viq.event.Event;
import com.service.viq.event.Event.CaseNumbers;
import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.DARNotifyEventResponse;
import com.viqsoultions.ObjectFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.hmcts.darts.event.model.DarNotifyEvent;
import uk.gov.hmcts.darts.log.api.impl.LogApiImpl;
import uk.gov.hmcts.darts.log.service.impl.DarNotificationLoggerServiceImpl;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.MALFORMED;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.NO_DESTINATION_DATA;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.NO_MATCHING_EVENT;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.OK;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.OTHER_ERROR;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.WRONG_DESTINATION;

@TestInstance(Lifecycle.PER_CLASS)
@SuppressWarnings("PMD.TooManyMethods")
class DarNotifyEventClientTest {

    private static final String EVENT_DATE_TIME_ATTRIBUTE = "%d";

    private DarNotifyEvent darNotifyEvent;
    private DARNotifyEvent request;
    private DARNotifyEventResponse response;
    private Event event;
    private final WebServiceTemplate mockWebServiceTemplate = mock(WebServiceTemplate.class);
    private DarNotifyEventClient darNotifyEventClient;

    @BeforeAll
    void beforeAll() {

        darNotifyEvent = DarNotifyEvent.builder()
            .notificationUrl("http://192.168.0.1:8080/VIQDARNotifyEvent/DARNotifyEvent.asmx")
            .notificationType("3")
            .timestamp(OffsetDateTime.parse("2023-06-19T14:52:40.637Z"))
            .courthouse("Test Court")
            .courtroom("1")
            .caseNumbers(List.of("A123456"))
            .build();

        ObjectFactory factory = new ObjectFactory();
        request = convertToXmlDarNotifyEvent();
        event = createEvent(darNotifyEvent);
        response = factory.createDARNotifyEventResponse();

        var logApi = new LogApiImpl(new DarNotificationLoggerServiceImpl());
        darNotifyEventClient = new DarNotifyEventClient(
            "http://www.viqsoultions.com/DARNotifyEvent",
            mockWebServiceTemplate,
            logApi,
            new DarNotifyEventClient.DarPcTimeLogInterceptor(Clock.systemDefaultZone()));
    }

    @Test
    void shouldHandleOkResultCode() {
        response.setDARNotifyEventResult(OK.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertTrue(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleMalformedResultCode() {
        response.setDARNotifyEventResult(MALFORMED.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleNoDestinationDataResultCode() {
        response.setDARNotifyEventResult(NO_DESTINATION_DATA.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleWrongDestinationResultCode() {
        response.setDARNotifyEventResult(WRONG_DESTINATION.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleNoMatchingEventResultCode() {
        response.setDARNotifyEventResult(NO_MATCHING_EVENT.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleOtherErrorResultCode() {
        response.setDARNotifyEventResult(OTHER_ERROR.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleUnexpectedResultCode() {
        response.setDARNotifyEventResult(-1);
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleNoResponse() {
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(null);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    @Test
    void shouldHandleWebServiceException() {
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenThrow(new WebServiceException("Something bad happened!") {
        });

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request, event));
    }

    private static DARNotifyEvent convertToXmlDarNotifyEvent() {
        ObjectFactory factory = new ObjectFactory();

        String xmlEventDocument = "some event document";

        DARNotifyEvent xmlDarNotifyEvent = factory.createDARNotifyEvent();
        xmlDarNotifyEvent.setXMLEventDocument(xmlEventDocument);

        return xmlDarNotifyEvent;
    }

    private static Event createEvent(DarNotifyEvent darNotifyEvent) {
        Event event = new Event();
        event.setType(darNotifyEvent.getNotificationType());

        OffsetDateTime localDateTime = OffsetDateTime.ofInstant(
            darNotifyEvent.getTimestamp().toInstant(),
            ZoneId.of("Europe/London")
        );
        event.setY(String.format(EVENT_DATE_TIME_ATTRIBUTE, localDateTime.getYear()));
        event.setM(String.format(EVENT_DATE_TIME_ATTRIBUTE, localDateTime.getMonthValue()));
        event.setD(String.format(EVENT_DATE_TIME_ATTRIBUTE, localDateTime.getDayOfMonth()));
        event.setH(String.format(EVENT_DATE_TIME_ATTRIBUTE, localDateTime.getHour()));
        event.setMIN(String.format(EVENT_DATE_TIME_ATTRIBUTE, localDateTime.getMinute()));
        event.setS(String.format(EVENT_DATE_TIME_ATTRIBUTE, localDateTime.getSecond()));

        event.setCourthouse(darNotifyEvent.getCourthouse());
        event.setCourtroom(darNotifyEvent.getCourtroom());

        CaseNumbers caseNumbers = new CaseNumbers();
        caseNumbers.getCaseNumber().addAll(darNotifyEvent.getCaseNumbers());
        event.setCaseNumbers(caseNumbers);

        return event;

    }

}

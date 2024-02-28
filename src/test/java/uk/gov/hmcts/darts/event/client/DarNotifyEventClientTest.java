package uk.gov.hmcts.darts.event.client;

import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.DARNotifyEventResponse;
import com.viqsoultions.Event;
import com.viqsoultions.Event.CaseNumbers;
import com.viqsoultions.ObjectFactory;
import com.viqsoultions.XMLEventDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.gov.hmcts.darts.event.config.DarNotifyEventConfigurationProperties;
import uk.gov.hmcts.darts.event.model.DarNotifyEvent;
import uk.gov.hmcts.darts.log.api.impl.LogApiImpl;
import uk.gov.hmcts.darts.log.service.impl.DarNotificationLoggerServiceImpl;

import java.net.MalformedURLException;
import java.net.URL;
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

    private final WebServiceTemplate mockWebServiceTemplate = mock(WebServiceTemplate.class);
    private DarNotifyEventClient darNotifyEventClient;

    @BeforeAll
    void beforeAll() throws MalformedURLException {
        var darNotifyEventConfigurationProperties = new DarNotifyEventConfigurationProperties();
        darNotifyEventConfigurationProperties.setSoapAction(new URL("http://www.VIQSoultions.com/DARNotifyEvent"));
        darNotifyEventConfigurationProperties.setSecurementActions("UsernameToken");
        darNotifyEventConfigurationProperties.setSecurementUsername("secure_user");
        darNotifyEventConfigurationProperties.setSecurementPassword("secure_password");

        darNotifyEvent = DarNotifyEvent.builder()
            .notificationUrl("http://192.168.0.1:8080/VIQDARNotifyEvent/DARNotifyEvent.asmx")
            .notificationType("3")
            .timestamp(OffsetDateTime.parse("2023-06-19T14:52:40.637Z"))
            .courthouse("Test Court")
            .courtroom("1")
            .caseNumbers(List.of("A123456"))
            .build();

        ObjectFactory factory = new ObjectFactory();
        request = convertToXmlDarNotifyEvent(darNotifyEvent);
        response = factory.createDARNotifyEventResponse();

        var logApi = new LogApiImpl(new DarNotificationLoggerServiceImpl());
        darNotifyEventClient = new DarNotifyEventClient(
            darNotifyEventConfigurationProperties,
            mockWebServiceTemplate,
            logApi);
    }

    @Test
    void shouldHandleOkResultCode() {
        response.setDARNotifyEventResult(OK.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertTrue(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleMalformedResultCode() {
        response.setDARNotifyEventResult(MALFORMED.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleNoDestinationDataResultCode() {
        response.setDARNotifyEventResult(NO_DESTINATION_DATA.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleWrongDestinationResultCode() {
        response.setDARNotifyEventResult(WRONG_DESTINATION.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleNoMatchingEventResultCode() {
        response.setDARNotifyEventResult(NO_MATCHING_EVENT.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleOtherErrorResultCode() {
        response.setDARNotifyEventResult(OTHER_ERROR.getResult());
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleUnexpectedResultCode() {
        response.setDARNotifyEventResult(-1);
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(response);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleNoResponse() {
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenReturn(null);

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    @Test
    void shouldHandleWebServiceException() {
        when(mockWebServiceTemplate.marshalSendAndReceive(
            eq(darNotifyEvent.getNotificationUrl()),
            any(DARNotifyEvent.class),
            any(SoapActionCallback.class)
        )).thenThrow(new WebServiceException("Something bad happened!") {
        });

        assertFalse(darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), request));
    }

    private static DARNotifyEvent convertToXmlDarNotifyEvent(DarNotifyEvent darNotifyEvent) {
        ObjectFactory factory = new ObjectFactory();

        Event event = factory.createEvent();
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

        CaseNumbers caseNumbers = factory.createEventCaseNumbers();
        caseNumbers.getCaseNumber().addAll(darNotifyEvent.getCaseNumbers());
        event.setCaseNumbers(caseNumbers);

        XMLEventDocument xmlEventDocument = factory.createXMLEventDocument();
        xmlEventDocument.setEvent(event);

        DARNotifyEvent xmlDarNotifyEvent = factory.createDARNotifyEvent();
        xmlDarNotifyEvent.setXMLEventDocument(xmlEventDocument);

        return xmlDarNotifyEvent;
    }

}

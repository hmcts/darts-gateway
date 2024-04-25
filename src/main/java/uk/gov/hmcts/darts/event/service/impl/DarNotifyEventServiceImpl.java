package uk.gov.hmcts.darts.event.service.impl;

import com.service.viq.event.Event;
import com.service.viq.event.Event.CaseNumbers;
import com.viqsoultions.DARNotifyEvent;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.event.client.DarNotifyEventClient;
import uk.gov.hmcts.darts.event.model.DarNotifyEvent;
import uk.gov.hmcts.darts.event.service.DarNotifyEventService;

import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@Service
@Slf4j
public class DarNotifyEventServiceImpl implements DarNotifyEventService {

    private static final String EVENT_DATE_TIME_ATTRIBUTE = "%d";

    private final DarNotifyEventClient darNotifyEventClient;

    @Value("${darts-gateway.events.dar-notify-event.enabled}")
    boolean enableDarNotify;

    @Override
    public void darNotify(DarNotifyEvent darNotifyEvent) {
        if (enableDarNotify) {
            var eventAsXml = createEvent(darNotifyEvent);
            var darNotifyEventAsXml = convertToXmlDarNotifyEvent(eventAsXml);

            darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), darNotifyEventAsXml, eventAsXml);
        }
    }

    private Event createEvent(DarNotifyEvent darNotifyEvent) {
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

    private DARNotifyEvent convertToXmlDarNotifyEvent(Event eventAsXml) {
        DARNotifyEvent xmlDarNotifyEvent = new DARNotifyEvent();
        xmlDarNotifyEvent.setXMLEventDocument(serialized(eventAsXml));

        return xmlDarNotifyEvent;
    }

    private String serialized(Event event) {
        var writer = new StringWriter();
        try {
            var context = JAXBContext.newInstance(Event.class);
            var marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            marshaller.marshal(event, writer);
        } catch (JAXBException e) {
            log.error("Error marshalling XML", e);
        }

        return writer.toString();
    }

}

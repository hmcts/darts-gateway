package uk.gov.hmcts.darts.event.service.impl;

import com.viqsoultions.DARNotifyEvent;
import com.viqsoultions.Event;
import com.viqsoultions.Event.CaseNumbers;
import com.viqsoultions.ObjectFactory;
import com.viqsoultions.XMLEventDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.event.client.DarNotifyEventClient;
import uk.gov.hmcts.darts.event.config.DarNotifyEventConfigurationProperties;
import uk.gov.hmcts.darts.event.model.DarNotifyEvent;
import uk.gov.hmcts.darts.event.service.DarNotifyEventService;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@Service
@Slf4j
public class DarNotifyEventServiceImpl implements DarNotifyEventService {

    private static final String EVENT_DATE_TIME_ATTRIBUTE = "%d";

    private final DarNotifyEventConfigurationProperties darNotifyEventConfigurationProperties;
    private final DarNotifyEventClient darNotifyEventClient;

    @Override
    public void darNotify(DarNotifyEvent darNotifyEvent) {
        DARNotifyEvent xmlDarNotifyEvent = convertToXmlDarNotifyEvent(darNotifyEvent);
        darNotifyEventClient.darNotifyEvent(darNotifyEvent.getNotificationUrl(), xmlDarNotifyEvent);
    }

    private DARNotifyEvent convertToXmlDarNotifyEvent(DarNotifyEvent darNotifyEvent) {
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

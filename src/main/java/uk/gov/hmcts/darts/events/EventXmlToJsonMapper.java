package uk.gov.hmcts.darts.events;

import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class EventXmlToJsonMapper {

    public EventRequest toJson(DartsEvent dartsEvent, String type, String subType) {
        return new EventRequest(
            type,
            subType,
            dartsEvent.getCourtHouse(),
            dartsEvent.getCourtRoom(),
            dartsEvent.getCaseNumbers().getCaseNumber(),
            dartsEvent.getEventText(),
            toLocalDateTime(dartsEvent)
        );
    }

    private static LocalDateTime toLocalDateTime(DartsEvent dartsEvent) {
        return LocalDateTime.of(
            dartsEvent.getY().intValue(),
            Month.of(dartsEvent.getM().intValue()),
            dartsEvent.getD().intValue(),
            dartsEvent.getH().intValue(),
            dartsEvent.getMIN().intValue(),
            dartsEvent.getS().intValue());
    }
}

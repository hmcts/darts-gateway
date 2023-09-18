package uk.gov.hmcts.darts.event.service.impl;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.model.event.EventsResponse;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class EventRequestMapper {

    public uk.gov.hmcts.darts.model.event.DartsEvent toNewApi(DartsEvent dartsEvent, String messageId, String type, String subType) {
        uk.gov.hmcts.darts.model.event.DartsEvent event = new uk.gov.hmcts.darts.model.event.DartsEvent();
        event.setMessageId(messageId);
        event.setType(type);
        event.setSubType(subType);
        event.setCourthouse(dartsEvent.getCourtHouse());
        event.setCourtroom(dartsEvent.getCourtRoom());
        event.setCourtroom(dartsEvent.getCourtRoom());
        event.setCaseNumbers(dartsEvent.getCaseNumbers().getCaseNumber());
        event.setEventText(dartsEvent.getEventText());
        event.setDateTime(toLocalDateTime(dartsEvent));

        return event;
    }

    private static OffsetDateTime toLocalDateTime(DartsEvent dartsEvent) {
        return OffsetDateTime.of(LocalDateTime.of(
            dartsEvent.getY().intValue(),
            Month.of(dartsEvent.getM().intValue()),
            dartsEvent.getD().intValue(),
            dartsEvent.getH().intValue(),
            dartsEvent.getMIN().intValue(),
            dartsEvent.getS().intValue()), ZoneOffset.UTC);
    }

    public DARTSResponse toLegacyAddDocumentResponse(EventsResponse eventResponse) {
        var dartsResponse = new DARTSResponse();
        dartsResponse.setMessage(eventResponse.getMessage());
        dartsResponse.setCode(eventResponse.getCode());

        return dartsResponse;
    }
}

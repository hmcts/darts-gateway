package uk.gov.hmcts.darts.event.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.model.event.DartsEventRetentionPolicy;
import uk.gov.hmcts.darts.utilities.DateUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;

@Service
public class EventRequestMapper {

    public uk.gov.hmcts.darts.model.event.DartsEvent toNewApi(DartsEvent dartsEvent, String messageId, String type, String subType) {
        uk.gov.hmcts.darts.model.event.DartsEvent event = new uk.gov.hmcts.darts.model.event.DartsEvent();
        event.setEventId(String.valueOf(dartsEvent.getID()));
        event.setMessageId(messageId);
        event.setType(type);
        event.setSubType(subType);
        event.setCourthouse(dartsEvent.getCourtHouse());
        event.setCourtroom(dartsEvent.getCourtRoom());
        event.setCaseNumbers(dartsEvent.getCaseNumbers().getCaseNumber());
        event.setEventText(dartsEvent.getEventText());
        event.setDateTime(toOffsetDateTime(dartsEvent));

        if (dartsEvent.getRetentionPolicy() != null) {
            event.setRetentionPolicy(toDartsEventRetentionPolicy(dartsEvent.getRetentionPolicy()));
        }

        return event;
    }

    private static OffsetDateTime toOffsetDateTime(DartsEvent dartsEvent) {
        LocalDateTime localDateTime = LocalDateTime.of(
            dartsEvent.getY().intValue(),
            Month.of(dartsEvent.getM().intValue()),
            dartsEvent.getD().intValue(),
            dartsEvent.getH().intValue(),
            dartsEvent.getMIN().intValue(),
            dartsEvent.getS().intValue()
        );
        //date comes in as localTime from xhibit.
        return DateUtil.toOffsetDateTime(localDateTime);
    }

    private DartsEventRetentionPolicy toDartsEventRetentionPolicy(DartsEvent.RetentionPolicy retentionPolicy) {
        DartsEventRetentionPolicy policy = new DartsEventRetentionPolicy();
        policy.setCaseTotalSentence(retentionPolicy.getCaseTotalSentence());
        policy.setCaseRetentionFixedPolicy(retentionPolicy.getCaseRetentionFixedPolicy());
        return policy;
    }
}

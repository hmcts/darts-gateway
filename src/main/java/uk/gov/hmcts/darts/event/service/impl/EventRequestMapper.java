package uk.gov.hmcts.darts.event.service.impl;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.synapps.moj.dfs.response.DARTSResponse;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.event.model.EventRequest;
import uk.gov.hmcts.darts.event.model.EventResponse;

import java.time.LocalDateTime;
import java.time.Month;

@Service
public class EventRequestMapper {

    public EventRequest toNewApi(DartsEvent dartsEvent, String messageId, String type, String subType) {
        return new EventRequest(
            messageId,
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
            dartsEvent.getS().intValue()
        );
    }

    public AddDocumentResponse toLegacyApi(EventResponse eventResponse) {
        var dartsResponse = new DARTSResponse();
        dartsResponse.setMessage(eventResponse.message());
        dartsResponse.setCode(eventResponse.code());

        var addDocumentResponse = new AddDocumentResponse();
        addDocumentResponse.setReturn(dartsResponse);

        return addDocumentResponse;
    }
}

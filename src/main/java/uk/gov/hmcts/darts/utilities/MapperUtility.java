package uk.gov.hmcts.darts.utilities;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.experimental.UtilityClass;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.courtservice.events.LogEntry;
import uk.gov.hmcts.darts.event.model.EventResponse;

import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
public class MapperUtility {

    public static LocalDateTime toLocalDateTime(LogEntry dartsEvent) {
        return LocalDateTime.of(
            dartsEvent.getY().intValue(),
            Month.of(dartsEvent.getM().intValue()),
            dartsEvent.getD().intValue(),
            dartsEvent.getH().intValue(),
            dartsEvent.getMIN().intValue(),
            dartsEvent.getS().intValue()
        );
    }

    public static AddLogEntryResponse mapResponse(EventResponse eventResponse) {
        var dartsResponse = new DARTSResponse();
        dartsResponse.setMessage(eventResponse.message());
        dartsResponse.setCode(eventResponse.code());

        var addLogEntryResponse = new AddLogEntryResponse();
        addLogEntryResponse.setReturn(dartsResponse);

        return addLogEntryResponse;
    }



}

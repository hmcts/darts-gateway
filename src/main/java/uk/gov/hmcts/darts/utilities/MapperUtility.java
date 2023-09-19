package uk.gov.hmcts.darts.utilities;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.experimental.UtilityClass;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.model.event.EventsResponse;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static uk.gov.hmcts.darts.utilities.DateUtil.LONDON_ZONE_ID;

@UtilityClass
@SuppressWarnings({"PMD.HideUtilityClassConstructor"})
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

    public static OffsetDateTime toOffsetDateTime(LogEntry logEntry) {
        LocalDateTime localDateTime = toLocalDateTime(logEntry);
        ZoneOffset zoneOffSet = LONDON_ZONE_ID.getRules().getOffset(localDateTime);
        return OffsetDateTime.of(localDateTime, zoneOffSet);

    }

    public static DARTSResponse mapResponse(EventsResponse eventResponse) {
        var dartsResponse = new DARTSResponse();
        dartsResponse.setMessage(eventResponse.getMessage());
        dartsResponse.setCode(eventResponse.getCode());

        return dartsResponse;
    }

}

package uk.gov.hmcts.darts.utilities;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.experimental.UtilityClass;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.model.event.EventsResponse;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static uk.gov.hmcts.darts.utilities.DateUtil.LONDON_ZONE_ID;

@UtilityClass
@SuppressWarnings({"HideUtilityClassConstructor"})
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

    public static DARTSResponse mapResponse(EventsResponse eventResponse, boolean convert201to200) {
        var dartsResponse = new DARTSResponse();
        if (convert201to200 && eventResponse.getCode().equals("201")) {
            dartsResponse.setCode(CodeAndMessage.OK.getCode());
            dartsResponse.setMessage(CodeAndMessage.OK.getMessage());
        } else {
            dartsResponse.setMessage(eventResponse.getMessage());
            dartsResponse.setCode(eventResponse.getCode());
        }
        return dartsResponse;
    }

    public static OffsetDateTime getAudioStartDateTime(Audio audio) {
        OffsetDateTime startDate = OffsetDateTime.of(
            Integer.valueOf(audio.getStart().getY()),
            Integer.valueOf(audio.getStart().getM()),
            Integer.valueOf(audio.getStart().getD()),
            Integer.valueOf(audio.getStart().getH()),
            Integer.valueOf(audio.getStart().getMIN()),
            Integer.valueOf(audio.getStart().getS()),
            0,
            ZoneOffset.UTC
        );
        return startDate;
    }

    public static OffsetDateTime getAudioEndDateTime(Audio audio) {
        OffsetDateTime finishDate = OffsetDateTime.of(
            audio.getEnd().getY().intValue(),
            audio.getEnd().getM().intValue(),
            audio.getEnd().getD().intValue(),
            audio.getEnd().getH().intValue(),
            audio.getEnd().getMIN().intValue(),
            audio.getEnd().getS().intValue(),
            0,
            ZoneOffset.UTC
        );
        return finishDate;
    }
}

package uk.gov.hmcts.darts.common.util;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.utilities.DateUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@SuppressWarnings("PMD.LawOfDemeter")
public class DateConverters {

    private static final DateTimeFormatter LEGACY_COURT_LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId ASSUMED_SOURCE_ZONE_ID = ZoneId.of("Europe/London");

    public OffsetDateTime offsetDateTimeFrom(final String timeString) {
        var localDateTime = LocalDateTime.parse(timeString, LEGACY_COURT_LOG_DATE_FORMAT);

        return localDateTime.atZone(ASSUMED_SOURCE_ZONE_ID)
              .toOffsetDateTime();
    }

    public OffsetDateTime offsetDateTimeFrom(Audio.Start start) {
        var localDateTime = LocalDateTime.of(Integer.parseInt(start.getY()),
                                             Month.of(Integer.parseInt(start.getM())), Integer.parseInt(start.getD()),
                                             Integer.parseInt(start.getH()), Integer.parseInt(start.getMIN()), Integer.parseInt(start.getS()));

        return DateUtil.toOffsetDateTime(localDateTime);
    }

    public OffsetDateTime offsetDateTimeFrom(Audio.End end) {
        var localDateTime = LocalDateTime.of(end.getY().intValue(), end.getM().intValue(), end.getD().intValue(),
                                             end.getH().intValue(), end.getMIN().intValue(), end.getS().intValue());
        return DateUtil.toOffsetDateTime(localDateTime);
    }

    public ZonedDateTime offsetDateTimeToLegacyDateTime(final OffsetDateTime offsetDateTime) {
        return offsetDateTime.atZoneSameInstant(ASSUMED_SOURCE_ZONE_ID);
    }


}

package uk.gov.hmcts.darts.utilities;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.XMLGregorianCalendar;

@UtilityClass
public class DateUtil {

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final ZoneId LONDON_ZONE_ID = ZoneId.of("Europe/London");

    public OffsetDateTime toOffsetDateTime(XMLGregorianCalendar date) {
        GregorianCalendar gregorianCalendar = date.toGregorianCalendar();

        //server always converts time as UTC, so this will make local testing perform the same.
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone(UTC));

        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        Instant instant = zonedDateTime.toInstant();
        //find out what timezone it should be in.
        ZoneOffset zoneOffSet = LONDON_ZONE_ID.getRules().getOffset(instant);
        //adjust to correct timezone
        instant = instant.minusSeconds(zoneOffSet.getTotalSeconds());
        return instant.atOffset(zoneOffSet);
    }

}

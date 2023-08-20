package uk.gov.hmcts.darts.utilities;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.XMLGregorianCalendar;

@UtilityClass
public class DateUtil {
    public OffsetDateTime toOffsetDateTime(XMLGregorianCalendar date) {
        GregorianCalendar gregorianCalendar = date.toGregorianCalendar();
        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        Instant instant = zonedDateTime.toInstant();
        ZoneId zone = ZoneId.of("Europe/London");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(instant);
        return instant.atOffset(zoneOffSet);
    }

    public OffsetDateTime toOffsetDateTime2(XMLGregorianCalendar date) {
        GregorianCalendar gregorianCalendar = date.toGregorianCalendar();
        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        return zonedDateTime.toOffsetDateTime();

    }
}

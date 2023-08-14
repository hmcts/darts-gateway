package uk.gov.hmcts.darts.utilities;

import lombok.experimental.UtilityClass;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

@UtilityClass
public class DateUtil {
    public OffsetDateTime toOffsetDateTime(XMLGregorianCalendar date) {
        GregorianCalendar gregorianCalendar = date.toGregorianCalendar();
        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        Instant instant = zonedDateTime.toInstant();
        OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);
        return offsetDateTime;
    }
}

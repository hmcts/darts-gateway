package uk.gov.hmcts.darts.utilities;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.XMLGregorianCalendar;

@UtilityClass
public class DateUtil {

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final ZoneId LONDON_ZONE_ID = ZoneId.of("Europe/London");

    public OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = toZonedDateTime(localDateTime);
        return toOffsetDateTime(zonedDateTime);
    }

    private OffsetDateTime toOffsetDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(UTC).toOffsetDateTime();
    }

    public OffsetDateTime toOffsetDateTime(XMLGregorianCalendar date) {
        GregorianCalendar gregorianCalendar = date.toGregorianCalendar();

        //server always converts time as UTC, so this will make local testing perform the same.
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone(UTC));

        ZonedDateTime zonedDateTime = gregorianCalendar.toZonedDateTime();
        Instant instant = zonedDateTime.toInstant();
        //find out what offset it should be in.
        ZoneOffset zoneOffSet = LONDON_ZONE_ID.getRules().getOffset(instant);
        //adjust to correct offset
        instant = instant.minusSeconds(zoneOffSet.getTotalSeconds());
        return instant.atOffset(zoneOffSet);
    }

    public LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
            .atZone(LONDON_ZONE_ID)
            .toLocalDate();
    }

    public LocalDate toLocalDate(String dateString) {
        Date date = null;
        try {
            date = DateUtils.parseDate(dateString, "yyyyMMdd", "yyyy-MM-dd");
            return toLocalDate(date);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date", e);
        }
    }

    public ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(LONDON_ZONE_ID);
    }

}

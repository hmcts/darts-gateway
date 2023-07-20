package uk.gov.hmcts.darts.common.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static java.util.TimeZone.getTimeZone;

@Component
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.SystemPrintln"})
public class DateConverters {

    private final SimpleDateFormat legacyCourtLogDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public OffsetDateTime offsetDateTimeFrom(final String timeString) {
        Date date;
        try {
            date = legacyCourtLogDateFormat.parse(timeString);
            System.out.println(date);
        } catch (ParseException e) {
            throw new DartsValidationException(e);
        }

        var zoneId = getTimeZone("Europe/London").toZoneId();
        System.out.println(zoneId);
        var zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), zoneId);
        System.out.println(zonedDateTime);

        OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
        System.out.println(offsetDateTime);
        System.out.println();
        return offsetDateTime;
    }
}

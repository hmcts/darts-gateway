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
@SuppressWarnings("PMD.LawOfDemeter")
public class DateConverters {

    private final SimpleDateFormat legacyCourtLogDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public OffsetDateTime offsetDateTimeFrom(final String timeString) {
        Date date;
        try {
            date = legacyCourtLogDateFormat.parse(timeString);
        } catch (ParseException e) {
            throw new DartsValidationException(e);
        }

        var zoneId = getTimeZone("Europe/London").toZoneId();
        var zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), zoneId);

        return zonedDateTime.toOffsetDateTime();
    }
}

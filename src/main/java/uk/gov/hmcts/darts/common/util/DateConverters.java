package uk.gov.hmcts.darts.common.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.Instant.ofEpochSecond;
import static java.util.TimeZone.getTimeZone;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@SuppressWarnings("PMD.LawOfDemeter")
public class DateConverters {

    private final SimpleDateFormat legacyCourtLogDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public OffsetDateTime offsetDateTimeFrom(final String timeString) {
        Instant instant;
        try {
            long timeSinceEpochInMillis = legacyCourtLogDateFormat.parse(timeString).getTime();
            instant = ofEpochSecond(MILLISECONDS.toSeconds(timeSinceEpochInMillis));
        } catch (ParseException e) {
            throw new DartsValidationException(e);
        }

        var zoneId = getTimeZone("Europe/London").toZoneId();

        return OffsetDateTime.ofInstant(instant, zoneId);
    }
}

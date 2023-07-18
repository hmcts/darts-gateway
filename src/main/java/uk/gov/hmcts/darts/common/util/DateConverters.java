package uk.gov.hmcts.darts.common.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.ofHours;
import static java.util.Calendar.ZONE_OFFSET;
import static java.util.TimeZone.getTimeZone;

@Component
@SuppressWarnings("PMD.LawOfDemeter")
public class DateConverters {

    private final SimpleDateFormat legacyCourtLogDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public OffsetDateTime offsetDateTimeFrom(final String timeString) {
        int offsetMillis = getTimeZone("Europe/London").getOffset(ZONE_OFFSET);
        long offsetHours = TimeUnit.MILLISECONDS.toHours(offsetMillis);
        OffsetDateTime offsetDateTime;
        try {
            offsetDateTime = legacyCourtLogDateFormat.parse(timeString)
                  .toInstant()
                  .atOffset(ofHours((int)offsetHours));
        } catch (ParseException e) {
            throw new DartsValidationException(e);
        }
        return offsetDateTime;
    }
}

package uk.gov.hmcts.darts.common.util;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static java.util.Calendar.ZONE_OFFSET;
import static java.util.TimeZone.getTimeZone;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class DateConvertersTest {

    DateConverters dateConverters = new DateConverters();

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeInLocalTimeZone() {
        int offsetMillis = getTimeZone("Europe/London").getOffset(ZONE_OFFSET);
        long offsetHours = MILLISECONDS.toHours(offsetMillis);

        OffsetDateTime offsetDateTime =
              dateConverters.offsetDateTimeFrom("20221228115959");

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(12);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(28);
        assertThat(offsetDateTime.getHour()).isEqualTo(11 + offsetHours);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);
    }
}
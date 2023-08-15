package uk.gov.hmcts.darts.common.util;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DateConvertersTest {

    private final DateConverters dateConverters = new DateConverters();

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsGmt() {
        var offsetDateTime = dateConverters.offsetDateTimeFrom("20221228115959");

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(12);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(28);
        assertThat(offsetDateTime.getHour()).isEqualTo(11);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(0));
    }

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsBst() {
        var offsetDateTime = dateConverters.offsetDateTimeFrom("20220628115959");

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(6);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(28);
        assertThat(offsetDateTime.getHour()).isEqualTo(11);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(1));
    }

    @Test
    void convertsUtcLocalDateTimeToLegacyDateTimeForGmt() {
        var utcDateTime = OffsetDateTime.parse("2022-01-11T16:00:00.000Z");

        var dateTime = dateConverters.offsetDateTimeToLegacyDateTime(utcDateTime);

        assertThat(dateTime.getHour()).isEqualTo(16);
    }

    @Test
    void convertsUtcLocalDateTimeToLegacyDateTimeForBst() {
        var utcDateTime = OffsetDateTime.parse("2022-06-11T16:00:00.000Z");

        var dateTime = dateConverters.offsetDateTimeToLegacyDateTime(utcDateTime);

        assertThat(dateTime.getHour()).isEqualTo(17);
    }
}

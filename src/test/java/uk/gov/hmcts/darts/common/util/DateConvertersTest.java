package uk.gov.hmcts.darts.common.util;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateConvertersTest {

    private final DateConverters dateConverters = new DateConverters();

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsGmt() {
        OffsetDateTime offsetDateTime = dateConverters.offsetDateTimeFrom("20221228115959");

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
        OffsetDateTime offsetDateTime = dateConverters.offsetDateTimeFrom("20220628115959");

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
        OffsetDateTime utcDateTime = OffsetDateTime.parse("2022-01-11T16:00:00.000Z");

        ZonedDateTime dateTime = dateConverters.offsetDateTimeToLegacyDateTime(utcDateTime);

        assertThat(dateTime.getHour()).isEqualTo(16);
    }

    @Test
    void convertsUtcLocalDateTimeToLegacyDateTimeForBst() {
        OffsetDateTime utcDateTime = OffsetDateTime.parse("2022-06-11T16:00:00.000Z");

        ZonedDateTime dateTime = dateConverters.offsetDateTimeToLegacyDateTime(utcDateTime);

        assertThat(dateTime.getHour()).isEqualTo(17);
    }

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsBst_WithAudioStartElement() {
        Audio.Start audio = new Audio.Start();
        audio.setY("2022");
        audio.setM("06");
        audio.setD("28");
        audio.setH("11");
        audio.setMIN("59");
        audio.setS("59");
        OffsetDateTime offsetDateTime = dateConverters.offsetDateTimeFrom(audio);

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(6);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(28);
        assertThat(offsetDateTime.getHour()).isEqualTo(11);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(1));
    }

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsGmt_WithAudioStartElement() {
        Audio.Start audio = new Audio.Start();
        audio.setY("2022");
        audio.setM("01");
        audio.setD("22");
        audio.setH("11");
        audio.setMIN("59");
        audio.setS("59");
        OffsetDateTime offsetDateTime = dateConverters.offsetDateTimeFrom(audio);

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(1);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(22);
        assertThat(offsetDateTime.getHour()).isEqualTo(11);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(0));
    }

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsBst_WithAudioEndElement() {
        Audio.End audio = new Audio.End();
        audio.setY(new BigInteger("2022"));
        audio.setM(new BigInteger("06"));
        audio.setD(new BigInteger("28"));
        audio.setH(new BigInteger("11"));
        audio.setMIN(new BigInteger("59"));
        audio.setS(new BigInteger("59"));
        OffsetDateTime offsetDateTime = dateConverters.offsetDateTimeFrom(audio);

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(6);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(28);
        assertThat(offsetDateTime.getHour()).isEqualTo(11);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(1));
    }

    @Test
    void convertsLegacyDateFormatToOffsetDateTimeWhenTimezoneIsGmt_WithAudioEndElement() {
        Audio.End audio = new Audio.End();
        audio.setY(new BigInteger("2022"));
        audio.setM(new BigInteger("01"));
        audio.setD(new BigInteger("22"));
        audio.setH(new BigInteger("11"));
        audio.setMIN(new BigInteger("59"));
        audio.setS(new BigInteger("59"));
        OffsetDateTime offsetDateTime = dateConverters.offsetDateTimeFrom(audio);

        assertThat(offsetDateTime.getYear()).isEqualTo(2022);
        assertThat(offsetDateTime.getMonthValue()).isEqualTo(1);
        assertThat(offsetDateTime.getDayOfMonth()).isEqualTo(22);
        assertThat(offsetDateTime.getHour()).isEqualTo(11);
        assertThat(offsetDateTime.getMinute()).isEqualTo(59);
        assertThat(offsetDateTime.getSecond()).isEqualTo(59);

        assertThat(offsetDateTime.getOffset()).isEqualTo(ZoneOffset.ofHours(0));
    }
}

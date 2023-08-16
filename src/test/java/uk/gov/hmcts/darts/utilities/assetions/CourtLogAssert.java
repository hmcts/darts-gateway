package uk.gov.hmcts.darts.utilities.assetions;

import com.synapps.moj.dfs.response.CourtLogEntry;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import uk.gov.hmcts.darts.model.courtLogs.CourtLog;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@SuppressWarnings("PMD.LinguisticNaming")
public class CourtLogAssert extends AbstractAssert<CourtLogAssert, com.synapps.moj.dfs.response.CourtLog> {

    public CourtLogAssert(com.synapps.moj.dfs.response.CourtLog actual) {
        super(actual, CourtLogAssert.class);
    }

    public CourtLogAssert assertCorrectNumberOfEntries(int numberOfEntries) {
        isNotNull();

        int actualLogSize = getLegacyEntriesFrom(actual).size();
        if (actualLogSize != numberOfEntries) {
            failWithMessage("Expected number of entries should be <%n> but was <%s>", numberOfEntries, actualLogSize);
        }

        return this;
    }

    public CourtLogAssert isCorrectlyMappedFrom(List<CourtLog> dartsApiCourtLogs) {
        assertCorrectNumberOfEntries(dartsApiCourtLogs.size());
        dartsApiCourtLogs.forEach(this::hasCorrectMapping);

        return this;
    }

    private void hasCorrectMapping(CourtLog newApiCourtLog) {
        var legacyCourtLog = getLegacyEntriesFrom(actual).stream()
              .filter((legacyLog) -> legacyLog.getValue().equals(newApiCourtLog.getEventText()))
              .findFirst().orElseThrow(
                    () -> new AssertionError("Expected a log with text: " + newApiCourtLog.getEventText()));

        OffsetDateTime expectedDateTime = newApiCourtLog.getTimestamp();

        Assertions.assertThat(actual.getCaseNumber()).isEqualTo(String.valueOf(newApiCourtLog.getCaseNumber()));
        Assertions.assertThat(actual.getCourthouse()).isEqualTo(String.valueOf(newApiCourtLog.getCourthouse()));
        Assertions.assertThat(legacyCourtLog.getY()).isEqualTo(String.valueOf(expectedDateTime.getYear()));
        Assertions.assertThat(legacyCourtLog.getM()).isEqualTo(String.valueOf(expectedDateTime.getMonthValue()));
        Assertions.assertThat(legacyCourtLog.getD()).isEqualTo(String.valueOf(expectedDateTime.getDayOfMonth()));
        Assertions.assertThat(getHourAdjustingForTimezone(legacyCourtLog)).isEqualTo(expectedDateTime.getHour());
        Assertions.assertThat(legacyCourtLog.getMIN()).isEqualTo(String.valueOf(expectedDateTime.getMinute()));
        Assertions.assertThat(legacyCourtLog.getS()).isEqualTo(String.valueOf(expectedDateTime.getSecond()));
    }

    public static int getHourAdjustingForTimezone(CourtLogEntry courtLogEntry) {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/London");

        // Create a Calendar instance and set the month and day
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023); // You can adjust the year as needed
        calendar.set(Calendar.MONTH, Integer.parseInt(courtLogEntry.getM()) - 1); // Month is 0-based in Calendar
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(courtLogEntry.getD()));

        // Check if the date is in daylight saving time
        var isDaylightSavingTime = timeZone.inDaylightTime(calendar.getTime());

        if (isDaylightSavingTime) {
            return Integer.parseInt(courtLogEntry.getH()) - 1;
        }

        return Integer.parseInt(courtLogEntry.getH());
    }

    // Hiding badly named method
    private static List<CourtLogEntry> getLegacyEntriesFrom(com.synapps.moj.dfs.response.CourtLog legacyCourtLog) {
        return legacyCourtLog.getEntry();
    }
}



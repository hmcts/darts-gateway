package uk.gov.hmcts.darts.utilities.assetions;

import com.synapps.moj.dfs.response.CourtLogEntry;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import uk.gov.hmcts.darts.model.courtLogs.CourtLogs;
import uk.gov.hmcts.darts.model.courtLogs.Entry;

import java.time.OffsetDateTime;
import java.util.List;

@SuppressWarnings("PMD.LinguisticNaming")
public class CourtLogEntriesAssert extends ListAssert<CourtLogEntry> {

    public CourtLogEntriesAssert(List<? extends CourtLogEntry> actual) {
        super(actual);
    }

    public CourtLogEntriesAssert hasNumberOfEntries(int numberOfEntries) {
        isNotNull();

        if (actual.size() != numberOfEntries) {
            failWithMessage("Expected number of entries should be <%n> but was <%s>", numberOfEntries, actual.size());
        }

        return this;
    }

    public CourtLogEntriesAssert isCorrectlyMappedFrom(CourtLogs dartsApiCourtLogs) {
        hasNumberOfEntries(dartsApiCourtLogs.getEntries().size());
        dartsApiCourtLogs.getEntries().forEach(this::checkContains);

        return this;
    }

    private void checkContains(Entry expectedEntry) {
        var courtLogEntry = actual.stream()
              .filter((actualEntry) -> actualEntry.getValue().equals(expectedEntry.getValue()))
              .findFirst().orElseThrow(
                    () -> new AssertionError("Expected a log with text: " + expectedEntry.getValue()));

        OffsetDateTime expectedDateTime = expectedEntry.getLogDateTime();

        Assertions.assertThat(courtLogEntry.getY()).isEqualTo(String.valueOf(expectedDateTime.getYear()));
        Assertions.assertThat(courtLogEntry.getM()).isEqualTo(String.valueOf(expectedDateTime.getMonthValue()));
        Assertions.assertThat(courtLogEntry.getD()).isEqualTo(String.valueOf(expectedDateTime.getDayOfMonth()));
        Assertions.assertThat(courtLogEntry.getH()).isEqualTo(String.valueOf(expectedDateTime.getHour()));
        Assertions.assertThat(courtLogEntry.getMIN()).isEqualTo(String.valueOf(expectedDateTime.getMinute()));
        Assertions.assertThat(courtLogEntry.getS()).isEqualTo(String.valueOf(expectedDateTime.getSecond()));
    }
}



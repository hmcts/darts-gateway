package uk.gov.hmcts.darts.courtlogs;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.model.courtLogs.CourtLogs;
import uk.gov.hmcts.darts.model.courtLogs.Entry;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.darts.utilities.assetions.CustomAssertions.verifyThat;

class GetCourtLogsMapperTest {

    OffsetDateTime today = OffsetDateTime.now();
    OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
    GetCourtLogsMapper courtLogsMapper = new GetCourtLogsMapper();

    @Test
    void mapsToLegacyApi() {
        var dartsApiCourtLogs = someCourtLogs();

        var legacyCourtLog = courtLogsMapper.toLegacyApi(dartsApiCourtLogs).getReturn().getCourtLog();

        assertThat(legacyCourtLog.getCaseNumber()).isEqualTo(dartsApiCourtLogs.getCaseNumber());
        assertThat(legacyCourtLog.getCourthouse()).isEqualTo(dartsApiCourtLogs.getCourthouse());
        verifyThat(legacyCourtLog.getEntry()).isCorrectlyMappedFrom(dartsApiCourtLogs);
    }

    private CourtLogs someCourtLogs() {
        var courtLogs = new CourtLogs();
        courtLogs.setCaseNumber("some-case-number");
        courtLogs.setCourthouse("some-courthouse");

        var entry1 = new Entry();
        entry1.setLogDateTime(today);
        entry1.setValue("some-text-1");
        courtLogs.addEntriesItem(entry1);

        var entry2 = new Entry();
        entry2.setLogDateTime(yesterday);
        entry2.setValue("some-text-2");
        courtLogs.addEntriesItem(entry2);

        return courtLogs;

    }
}
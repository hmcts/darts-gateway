package uk.gov.hmcts.darts.courtlogs;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.model.courtLogs.CourtLog;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static uk.gov.hmcts.darts.utilities.assetions.CustomAssertions.verifyThat;

class GetCourtLogsMapperTest {

    private final OffsetDateTime today = OffsetDateTime.now();
    private final GetCourtLogsMapper courtLogsMapper = new GetCourtLogsMapper();

    @Test
    void mapsToLegacyApi() {
        var dartsApiCourtLogs = someCourtLogs(2);

        var legacyCourtLog = courtLogsMapper.toLegacyApi(dartsApiCourtLogs).getReturn().getCourtLog();

        verifyThat(legacyCourtLog).isCorrectlyMappedFrom(dartsApiCourtLogs);
    }

    private List<CourtLog> someCourtLogs(int quantity) {
        return rangeClosed(1, quantity)
              .mapToObj((index) -> createCourtLog(today.minusDays(index), "some-text-" + index))
              .collect(toList());
    }

    private CourtLog createCourtLog(OffsetDateTime today, String eventText) {
        var courtLog1 = new CourtLog();
        courtLog1.setTimestamp(today);
        courtLog1.setEventText(eventText);
        courtLog1.setCaseNumber("some-case-number");
        courtLog1.setCourthouse("some-courthouse");
        return courtLog1;
    }
}
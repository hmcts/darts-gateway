package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.util.DateConverters;
import uk.gov.hmcts.darts.model.event.CourtLog;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.darts.utilities.assertions.CustomAssertions.verifyThat;

class GetCourtLogsMapperTest {

    private final OffsetDateTime todayInUtc = OffsetDateTime.now(ZoneOffset.UTC);
    private final GetCourtLogsMapper courtLogsMapper = new GetCourtLogsMapper(new DateConverters());

    @Test
    void mapsEmptyCourtLogsToLegacyApi() {
        var emptyCourtLogs = new ArrayList<CourtLog>();

        var legacyCourtLog = courtLogsMapper.toLegacyApi(emptyCourtLogs);

        assertThat(legacyCourtLog).isInstanceOf(GetCourtLogResponse.class);
        assertThat(legacyCourtLog.getReturn().getCourtLog()).isNull();
    }

    @Test
    void mapsToLegacyApi() {
        var dartsApiCourtLogs = someCourtLogs(2);

        var legacyCourtLog = courtLogsMapper.toLegacyApi(dartsApiCourtLogs).getReturn().getCourtLog();

        verifyThat(legacyCourtLog).isCorrectlyMappedFrom(dartsApiCourtLogs);
    }

    private List<CourtLog> someCourtLogs(int quantity) {
        return rangeClosed(1, quantity)
              .mapToObj((index) -> createCourtLog(todayInUtc.minusDays(index), "some-text-" + index))
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

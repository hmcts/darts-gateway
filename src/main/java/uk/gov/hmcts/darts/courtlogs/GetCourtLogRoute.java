package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLog;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.client.CourtLogsClient;
import uk.gov.hmcts.darts.common.util.DateConverters;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class GetCourtLogRoute {

    private final CourtLogsClient dartsFeignClient;
    private final GetCourtLogsMapper getCourtLogsMapper;
    private final DateConverters dateConverters;

    public GetCourtLogResponse route(GetCourtLog legacyGetCourtLog) {
        var courthouse = legacyGetCourtLog.getCourthouse();
        var caseNumber = legacyGetCourtLog.getCaseNumber();
        var startDateTime = dateConverters.offsetDateTimeFrom(legacyGetCourtLog.getStartTime());
        var endDateTime = dateConverters.offsetDateTimeFrom(legacyGetCourtLog.getEndTime());

        var dartsApiCourtLogs = dartsFeignClient.courtlogsGet(
            courthouse,
            caseNumber,
            OffsetDateTime.parse(startDateTime.toString()),
            OffsetDateTime.parse(endDateTime.toString()));

        return getCourtLogsMapper.toLegacyApi(dartsApiCourtLogs.getBody());
    }
}

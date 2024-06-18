package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLog;
import com.synapps.moj.dfs.response.GetCourtLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.client.CourtLogsClient;
import uk.gov.hmcts.darts.common.util.DateConverters;
import uk.gov.hmcts.darts.model.event.CourtLog;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GetCourtLogRoute {

    private final CourtLogsClient courtLogsClient;
    private final GetCourtLogsMapper getCourtLogsMapper;
    private final DateConverters dateConverters;

    public GetCourtLogResponse route(GetCourtLog legacyGetCourtLog) {
        String courthouse = legacyGetCourtLog.getCourthouse();
        String caseNumber = legacyGetCourtLog.getCaseNumber();
        OffsetDateTime startDateTime = dateConverters.offsetDateTimeFrom(legacyGetCourtLog.getStartTime());
        OffsetDateTime endDateTime = dateConverters.offsetDateTimeFrom(legacyGetCourtLog.getEndTime());

        ResponseEntity<List<CourtLog>> dartsApiCourtLogs = courtLogsClient.courtlogsGet(
            courthouse,
            caseNumber,
            OffsetDateTime.parse(startDateTime.toString()),
            OffsetDateTime.parse(endDateTime.toString()));

        return getCourtLogsMapper.toLegacyApi(dartsApiCourtLogs.getBody());
    }
}

package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLog;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.common.util.DateConverters;

@RequiredArgsConstructor
@Service
public class GetCourtLogRoute {

    private final DartsFeignClient dartsFeignClient;
    private final GetCourtLogsMapper getCourtLogsMapper;
    private final DateConverters dateConverters;

    public GetCourtLogResponse route(GetCourtLog legacyGetCourtLog) {
        var courthouse = legacyGetCourtLog.getCourthouse();
        var caseNumber = legacyGetCourtLog.getCaseNumber();
        var startDateTime = dateConverters.offsetDateTimeFrom(legacyGetCourtLog.getStartTime());
        var endDateTime = dateConverters.offsetDateTimeFrom(legacyGetCourtLog.getEndTime());

        var dartsApiCourtLogs = dartsFeignClient.getCourtLogs(
              courthouse,
              caseNumber,
              startDateTime,
              endDateTime);

        return getCourtLogsMapper.toLegacyApi(dartsApiCourtLogs);
    }
}

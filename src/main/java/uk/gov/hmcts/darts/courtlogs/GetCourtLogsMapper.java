package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.synapps.moj.dfs.response.CourtLogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.util.DateConverters;
import uk.gov.hmcts.darts.model.events.CourtLog;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.LawOfDemeter")
public class GetCourtLogsMapper {

    private final DateConverters dateConverters;

    public GetCourtLogResponse toLegacyApi(List<CourtLog> courtLogs) {
        if (courtLogs.isEmpty()) {
            return emptyResponse();
        }
        var legacyCourtLog = createLegacyCourtLog();
        legacyCourtLog.setCourthouse(courtLogs.get(0).getCourthouse());
        legacyCourtLog.setCaseNumber(courtLogs.get(0).getCaseNumber());
        courtLogs.forEach((courtLog) -> legacyCourtLog.addCourtLogEntry(toLegacyApi(courtLog)));

        var innerResponse = createInnerResponse();
        innerResponse.setCourtLog(legacyCourtLog);
        innerResponse.setCode(String.valueOf(HttpStatus.OK.value()));
        innerResponse.setMessage(HttpStatus.OK.name());

        var outerResponse = new GetCourtLogResponse();
        outerResponse.setReturn(innerResponse);

        return outerResponse;
    }

    private CourtLogEntry toLegacyApi(CourtLog courtLog) {
        var logDateTime =
            dateConverters.offsetDateTimeToLegacyDateTime(courtLog.getTimestamp());

        var legacyCourtLogEntry = new CourtLogEntry();

        legacyCourtLogEntry.setY(String.valueOf(logDateTime.getYear()));
        legacyCourtLogEntry.setM(String.valueOf(logDateTime.getMonthValue()));
        legacyCourtLogEntry.setD(String.valueOf(logDateTime.getDayOfMonth()));
        legacyCourtLogEntry.setH(String.valueOf(logDateTime.getHour()));
        legacyCourtLogEntry.setMIN(String.valueOf(logDateTime.getMinute()));
        legacyCourtLogEntry.setS(String.valueOf(logDateTime.getSecond()));
        legacyCourtLogEntry.setValue(courtLog.getEventText());

        return legacyCourtLogEntry;
    }

    private GetCourtLogResponse emptyResponse() {
        var innerResponse = createInnerResponse();
        var outerResponse = new GetCourtLogResponse();
        outerResponse.setReturn(innerResponse);

        return outerResponse;
    }

    private com.synapps.moj.dfs.response.CourtLog createLegacyCourtLog() {
        return new com.synapps.moj.dfs.response.CourtLog();
    }

    private com.synapps.moj.dfs.response.GetCourtLogResponse createInnerResponse() {
        return new com.synapps.moj.dfs.response.GetCourtLogResponse();
    }
}

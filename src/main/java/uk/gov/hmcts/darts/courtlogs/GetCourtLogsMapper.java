package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.synapps.moj.dfs.response.CourtLog;
import com.synapps.moj.dfs.response.CourtLogEntry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.model.courtLogs.CourtLogs;
import uk.gov.hmcts.darts.model.courtLogs.Entry;

@Service
@SuppressWarnings("PMD.LawOfDemeter")
public class GetCourtLogsMapper {

    public GetCourtLogResponse toLegacyApi(CourtLogs courtLogs) {
        var legacyCourtLog = new CourtLog();
        legacyCourtLog.setCourthouse(courtLogs.getCourthouse());
        legacyCourtLog.setCaseNumber(courtLogs.getCaseNumber());
        courtLogs.getEntries()
              .forEach((logEntry) -> legacyCourtLog.addCourtLogEntry(toLegacyApi(logEntry)));

        var innerResponse = createInnerResponse();
        innerResponse.setCourtLog(legacyCourtLog);
        innerResponse.setCode(String.valueOf(HttpStatus.OK.value()));
        innerResponse.setMessage(HttpStatus.OK.name());

        var outerResponse = new GetCourtLogResponse();
        outerResponse.setReturn(innerResponse);

        return outerResponse;
    }

    private CourtLogEntry toLegacyApi(Entry logEntry) {
        var logDateTime = logEntry.getLogDateTime();
        var legacyCourtLogEntry = new CourtLogEntry();

        legacyCourtLogEntry.setY(String.valueOf(logDateTime.getYear()));
        legacyCourtLogEntry.setM(String.valueOf(logDateTime.getMonthValue()));
        legacyCourtLogEntry.setD(String.valueOf(logDateTime.getDayOfMonth()));
        legacyCourtLogEntry.setH(String.valueOf(logDateTime.getHour()));
        legacyCourtLogEntry.setMIN(String.valueOf(logDateTime.getMinute()));
        legacyCourtLogEntry.setS(String.valueOf(logDateTime.getSecond()));
        legacyCourtLogEntry.setValue(logEntry.getValue());

        return legacyCourtLogEntry;
    }

    private com.synapps.moj.dfs.response.GetCourtLogResponse createInnerResponse() {
        return new com.synapps.moj.dfs.response.GetCourtLogResponse();
    }
}

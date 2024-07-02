package uk.gov.hmcts.darts.courtlogs;


import com.synapps.moj.dfs.response.CourtLogEntry;
import com.synapps.moj.dfs.response.GetCourtLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.util.DateConverters;
import uk.gov.hmcts.darts.model.event.CourtLog;

import java.time.ZonedDateTime;
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
        com.synapps.moj.dfs.response.CourtLog legacyCourtLog = createLegacyCourtLog();
        legacyCourtLog.setCourthouse(courtLogs.get(0).getCourthouse());
        legacyCourtLog.setCaseNumber(courtLogs.get(0).getCaseNumber());
        courtLogs.forEach((courtLog) -> legacyCourtLog.getEntry().add(toLegacyApi(courtLog)));

        GetCourtLogResponse innerResponse = createInnerResponse();
        innerResponse.setCourtLog(legacyCourtLog);
        innerResponse.setCode(String.valueOf(HttpStatus.OK.value()));
        innerResponse.setMessage(HttpStatus.OK.name());

        return innerResponse;
    }

    private CourtLogEntry toLegacyApi(CourtLog courtLog) {
        CourtLogEntry legacyCourtLogEntry = new CourtLogEntry();
        ZonedDateTime logDateTime =
            dateConverters.offsetDateTimeToLegacyDateTime(courtLog.getTimestamp());

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
        return createInnerResponse();
    }

    private com.synapps.moj.dfs.response.CourtLog createLegacyCourtLog() {
        return new com.synapps.moj.dfs.response.CourtLog();
    }

    private GetCourtLogResponse createInnerResponse() {
        return new GetCourtLogResponse();
    }
}

package uk.gov.hmcts.darts.courtlogs;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.model.events.CourtLogsPostRequestBody;
import uk.gov.hmcts.darts.utilities.MapperUtility;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AddCourtLogsMapper {

    public CourtLogsPostRequestBody mapToApi(LogEntry logEntry) {

        CourtLogsPostRequestBody requestBody = new CourtLogsPostRequestBody();

        requestBody.setCourthouse(logEntry.getCourthouse());
        requestBody.setCourtroom(logEntry.getCourtroom());
        requestBody.setText(logEntry.getText());
        requestBody.setCaseNumbers(List.of(logEntry.getCaseNumbers().toString()));
        requestBody.setLogEntryDateTime(OffsetDateTime.of(MapperUtility.toLocalDateTime(logEntry), ZoneOffset.of("UTC")));

        return requestBody;
    }

}

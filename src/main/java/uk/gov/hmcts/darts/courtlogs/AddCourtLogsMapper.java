package uk.gov.hmcts.darts.courtlogs;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.model.event.CourtLogsPostRequestBody;
import uk.gov.hmcts.darts.utilities.MapperUtility;

@Service
public class AddCourtLogsMapper {

    public CourtLogsPostRequestBody mapToApi(LogEntry logEntry) {

        CourtLogsPostRequestBody requestBody = new CourtLogsPostRequestBody();

        requestBody.setCourthouse(logEntry.getCourthouse());
        requestBody.setCourtroom(logEntry.getCourtroom());
        requestBody.setText(logEntry.getText());
        requestBody.setCaseNumbers(logEntry.getCaseNumbers().getCaseNumber());

        requestBody.setLogEntryDateTime(MapperUtility.toOffsetDateTime(logEntry));

        return requestBody;
    }

}

package uk.gov.hmcts.darts.courtlogs;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.model.event.CourtLogsPostRequestBody;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddCourtLogsMapperTest {

    private final AddCourtLogsMapper addCourtLogsMapper = new AddCourtLogsMapper();

    @Test
    void mapToApiWithBstDate() {
        LogEntry logEntry = new LogEntry();
        logEntry.setD(BigInteger.valueOf(1));
        logEntry.setM(BigInteger.valueOf(7));
        logEntry.setY(BigInteger.valueOf(2023));
        logEntry.setH(BigInteger.valueOf(10));
        logEntry.setMIN(BigInteger.valueOf(0));
        logEntry.setS(BigInteger.valueOf(0));
        logEntry.setCaseNumbers(new LogEntry.CaseNumbers());

        CourtLogsPostRequestBody result = addCourtLogsMapper.mapToApi(logEntry);
        LocalDateTime expected = LocalDateTime.of(2023, Month.JULY, 1, 10, 0);
        assertEquals(expected, result.getLogEntryDateTime().toLocalDateTime());
        assertEquals(ZoneOffset.ofHours(1), result.getLogEntryDateTime().getOffset());

    }


    @Test
    void mapToApiWithGmtDate() {
        LogEntry logEntry = new LogEntry();
        logEntry.setD(BigInteger.valueOf(1));
        logEntry.setM(BigInteger.valueOf(1));
        logEntry.setY(BigInteger.valueOf(2023));
        logEntry.setH(BigInteger.valueOf(10));
        logEntry.setMIN(BigInteger.valueOf(0));
        logEntry.setS(BigInteger.valueOf(0));
        logEntry.setCaseNumbers(new LogEntry.CaseNumbers());

        CourtLogsPostRequestBody result = addCourtLogsMapper.mapToApi(logEntry);
        LocalDateTime expected = LocalDateTime.of(2023, Month.JANUARY, 1, 10, 0);
        assertEquals(expected, result.getLogEntryDateTime().toLocalDateTime());
        assertEquals(ZoneOffset.ofHours(0), result.getLogEntryDateTime().getOffset());

    }
}

package uk.gov.hmcts.darts.log.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.log.service.DarNotificationLoggerService;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public class DarNotificationLoggerServiceImpl implements DarNotificationLoggerService {

    @Override
    public void notificationSucceeded(String uri, String courthouse, String courtroom, String caseNumber, OffsetDateTime offsetDateTime, int responseCode) {
        log.info("DAR Notify: uri={}, courthouse={}, courtroom={}, caseNumber={}, date_time={}, response_status=OK, responseCode={}",
                uri,
                courthouse,
                courtroom,
                caseNumber,
                getDateTimeIsoFormatted(offsetDateTime),
                responseCode);
    }

    @Override
    public void notificationFailed(
            String uri, String courthouse, String courtroom, String caseNumber, OffsetDateTime offsetDateTime, String status, String message, Level logLevel) {
        log.atLevel(logLevel).log("DAR Notify: uri={}, courthouse={}, courtroom={}, caseNumber={}, date_time={}, response_status={}, message={}",
                uri,
                courthouse,
                courtroom,
                caseNumber,
                getDateTimeIsoFormatted(offsetDateTime),
                status,
                message);
    }

    @Override
    public void notificationFailedWithCode(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime,
            String status,
            String message,
            int responseCode,
            Level logLevel) {
        log.atLevel(logLevel).log("DAR Notify: uri={}, courthouse={}, courtroom={}, caseNumber={}, date_time={}, response_status={}, message={}, " +
                                      "responseCode={}",
                uri,
                courthouse,
                courtroom,
                caseNumber,
                getDateTimeIsoFormatted(offsetDateTime),
                status,
                message,
                responseCode);
    }

    private static String getDateTimeIsoFormatted(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}

package uk.gov.hmcts.darts.log.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.log.service.DarNotificationLoggerService;

import java.time.OffsetDateTime;

@Service
@Slf4j
public class DarNotificationLoggerServiceImpl implements DarNotificationLoggerService {

    @Override
    public void notificationSucceeded(String uri, String courthouse, String courtroom, String caseNumber, OffsetDateTime offsetDateTime) {
        log.info("DAR Notify: uri={}, courthouse={}, courtroom={}, caseNumber={}, date_time={}, response_status=OK",
                uri,
                courthouse,
                courtroom,
                caseNumber,
                offsetDateTime);
    }

    @Override
    public void notificationFailed(
            String uri, String courthouse, String courtroom, String caseNumber, OffsetDateTime offsetDateTime, String status, String message, Level logLevel) {
        log.atLevel(logLevel).log("DAR Notify: uri={}, courthouse={}, courtroom={}, caseNumber={}, date_time={}, response_status={}, message={}",
                uri,
                courthouse,
                courtroom,
                caseNumber,
                offsetDateTime,
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
            int code,
            Level logLevel) {

        log.atLevel(logLevel).log("DAR Notify: uri={}, courthouse={}, courtroom={}, caseNumber={}, date_time={}, response_status={}, message={}, code={}",
                uri,
                courthouse,
                courtroom,
                caseNumber,
                offsetDateTime,
                status,
                message,
                code);
    }
}

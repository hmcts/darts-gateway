package uk.gov.hmcts.darts.log.api.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.log.service.DarNotificationLoggerService;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public class LogApiImpl implements LogApi {

    private final DarNotificationLoggerService darNotificationLogger;

    @Override
    public void notificationSucceeded(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime,
            int responseCode) {

        darNotificationLogger.notificationSucceeded(uri, courthouse, courtroom, caseNumber, offsetDateTime, responseCode);
    }

    @Override
    public void notificationFailed(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime,
            String status,
            String message,
            Level logLevel) {

        darNotificationLogger.notificationFailed(uri, courthouse, courtroom, caseNumber, offsetDateTime, status, message, logLevel);
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

        darNotificationLogger.notificationFailedWithCode(uri, courthouse, courtroom, caseNumber, offsetDateTime, status, message, responseCode, logLevel);
    }
}

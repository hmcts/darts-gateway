package uk.gov.hmcts.darts.log.service;

import org.slf4j.event.Level;

import java.time.OffsetDateTime;

public interface DarNotificationLoggerService {

    void notificationSucceeded(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime,
            int responseCode);

    void notificationFailed(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime,
            String status,
            String message,
            Level logLevel);

    void notificationFailedWithCode(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime,
            String status,
            String message,
            int responseCode,
            Level logLevel);
}

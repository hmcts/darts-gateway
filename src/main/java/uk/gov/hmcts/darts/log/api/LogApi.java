package uk.gov.hmcts.darts.log.api;

import org.slf4j.event.Level;

import java.time.OffsetDateTime;

public interface LogApi {

    void notificationSucceeded(
            String uri,
            String courthouse,
            String courtroom,
            String caseNumber,
            OffsetDateTime offsetDateTime);

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
            int code,
            Level logLevel);
}

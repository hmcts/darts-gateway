package uk.gov.hmcts.darts.log.api.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.log.service.DarNotificationLoggerService;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.UseObjectForClearerAPI")
@Slf4j
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

    @Override
    public void failedToLinkAudioToCases(String courthouse, String courtroom, OffsetDateTime startedAt, OffsetDateTime endedAt, List<String> cases,
                                         String checksum, UUID storageBlobId) {
        log.error("Failed to link audio to cases: courthouse={}, courtroom={}, startedAt={}, endedAt={}, cases={}, checksum={}, blob_store_id={}",
                  courthouse,
                  courtroom,
                  startedAt.format(DateTimeFormatter.ISO_DATE_TIME),
                  endedAt.format(DateTimeFormatter.ISO_DATE_TIME),
                  cases,
                  checksum,
                  storageBlobId);
    }
}

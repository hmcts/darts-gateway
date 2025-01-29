package uk.gov.hmcts.darts.log.api;

import org.slf4j.event.Level;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("PMD.UseObjectForClearerAPI")
public interface LogApi {

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
        int code,
        Level logLevel);

    void failedToLinkAudioToCases(String courthouse, String courtroom, OffsetDateTime startedAt, OffsetDateTime endedAt, List<String> cases,
                                  String checksum, UUID storageBlobId);
}

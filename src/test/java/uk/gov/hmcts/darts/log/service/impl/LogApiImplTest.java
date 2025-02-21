package uk.gov.hmcts.darts.log.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.log.api.impl.LogApiImpl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(OutputCaptureExtension.class)
class LogApiImplTest {


    @Test
    void failedToLinkAudioToCases_shouldLogFailedToLinkAudioToCasesMessage(CapturedOutput output) {

        LogApi logApi = new LogApiImpl(null);
        OffsetDateTime startAt = OffsetDateTime.parse("2025-01-29T10:44:25.028498Z");
        OffsetDateTime endAt = OffsetDateTime.parse("2025-01-31T10:44:25.028526Z");
        UUID storageUuid = UUID.fromString("2250b3a7-933f-4358-a770-5f5ebf3c17b9");

        logApi.failedToLinkAudioToCases(
            "courtHouse",
            "courtroom",
            startAt,
            endAt,
            List.of("case1", "case2"),
            "checksum",
            storageUuid
        );

        waitUntilMessage(output,
                         "Failed to link audio to cases: " +
                             "courthouse=courtHouse, " +
                             "courtroom=courtroom, " +
                             "started_at=2025-01-29T10:44:25.028498Z, " +
                             "ended_at=2025-01-31T10:44:25.028526Z, " +
                             "cases=[case1, case2], " +
                             "checksum=checksum, " +
                             "blob_store_id=2250b3a7-933f-4358-a770-5f5ebf3c17b9",
                         5);
    }

    @SneakyThrows
    @SuppressWarnings("PMD.DoNotUseThreads")//Required to prevent busy waiting
    //Used to allow logs to catch up with the test
    public static void waitUntilMessage(CapturedOutput capturedOutput, String message,
                                        int timeoutInSeconds) {
        long startTime = System.currentTimeMillis();
        while (!capturedOutput.getAll().contains(message)) {
            if (System.currentTimeMillis() - startTime > timeoutInSeconds * 1000) {
                fail("Timeout waiting for message: " + message);
            }
            Thread.sleep(100);
        }
    }
}

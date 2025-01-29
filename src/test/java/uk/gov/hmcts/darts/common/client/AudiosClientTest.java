package uk.gov.hmcts.darts.common.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequestWithStorageGUID;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AudiosClientTest {

    @Mock
    private LogApi logApi;
    @Mock
    private DartsClientProblemDecoder dartsClientProblemDecoder;

    @InjectMocks
    @Spy
    private AudiosClient audiosClient;

    @Test
    void addAudioMetaData_shouldLogFailedToLinkAudioToCasesMessage_onAnyFailure() {
        doThrow(new TestHttpStatusCodeException()).when(audiosClient)
            .processHttpHeaderInterceptors(any());
        OffsetDateTime startAt = OffsetDateTime.now();
        OffsetDateTime endAt = OffsetDateTime.now().plusDays(2);
        UUID storageUuid = UUID.randomUUID();

        AddAudioMetadataRequestWithStorageGUID request = new AddAudioMetadataRequestWithStorageGUID();
        request.setCourthouse("courtHouse");
        request.setCourtroom("courtroom");
        request.setStartedAt(startAt);
        request.setEndedAt(endAt);
        request.setCases(List.of("case1", "case2"));
        request.setChecksum("checksum");
        request.setStorageGuid(storageUuid);

        assertThrows(Exception.class, () -> audiosClient.addAudioMetaData(request));

        verify(logApi)
            .failedToLinkAudioToCases(
                "courtHouse",
                "courtroom",
                startAt,
                endAt,
                List.of("case1", "case2"),
                "checksum",
                storageUuid
            );
    }


    @SuppressWarnings("PMD.TestClassWithoutTestCases")//False positive
    public static class TestHttpStatusCodeException extends HttpStatusCodeException {
        protected TestHttpStatusCodeException() {
            super(HttpStatus.BAD_REQUEST);
        }
    }
}

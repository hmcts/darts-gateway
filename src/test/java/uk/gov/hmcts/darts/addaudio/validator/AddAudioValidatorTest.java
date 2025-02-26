package uk.gov.hmcts.darts.addaudio.validator;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class AddAudioValidatorTest {

    @Test
    void validateSize_shouldNotThrowException_whenSizeIsLessThanMaxSize() {
        LogApi logApi = mock(LogApi.class);
        AddAudioValidator validator = new AddAudioValidator(null, null, logApi);
        validator.expectedFileSize = 1000;
        AddAudioMetadataRequest metaData = mock(AddAudioMetadataRequest.class);

        validator.validateSize(metaData, 900);
        verifyNoInteractions(logApi);
    }

    @Test
    void validateSize_shouldThrowException_whenSizeIsGreaterThanMaxSize() {
        LogApi logApi = mock(LogApi.class);
        AddAudioValidator validator = new AddAudioValidator(null, null, logApi);
        validator.expectedFileSize = 1000;
        AddAudioMetadataRequest metaData = new AddAudioMetadataRequest();
        metaData.setCourthouse("courthouse");
        metaData.setCourtroom("courtroom");
        OffsetDateTime startedAt = OffsetDateTime.now();
        metaData.setStartedAt(startedAt);
        OffsetDateTime endedAt = OffsetDateTime.now().plusDays(1);
        metaData.setEndedAt(endedAt);
        metaData.setCases(List.of("case1", "case2"));
        metaData.setChecksum("checksum");

        DartsValidationException dartsValidationException =
            assertThrows(DartsValidationException.class,
                         () -> validator.validateSize(metaData, AddAudioValidator.getBytes(1001)));

        assertThat(dartsValidationException.getCodeAndMessage()).isEqualTo(CodeAndMessage.AUDIO_TOO_LARGE);
        verify(logApi).failedToLinkAudioToCases(
            "courthouse",
            "courtroom",
            startedAt,
            endedAt,
            List.of("case1", "case2"),
            "checksum",
            null
        );
    }
}

package uk.gov.hmcts.darts.addaudio.validator;

import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.addaudio.Audio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.config.AllowedMediaConfig;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import static uk.gov.hmcts.darts.utilities.MapperUtility.getAudioEndDateTime;
import static uk.gov.hmcts.darts.utilities.MapperUtility.getAudioStartDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(AllowedMediaConfig.class)
public class AddAudioValidator {

    @Value("${darts-gateway.add-audio.schema}")
    private String addAudioSchema;
    @Value("${darts-gateway.add-audio.validate}")
    private boolean validateAddAudio;
    @Value("${darts-gateway.add-audio.fileSizeInMegaBytes}")
    private long expectedFileSize;
    @Value("${darts-gateway.add-audio.maxFileDuration}")
    private Duration maxFileDuration;

    private final XmlWithFileMultiPartRequestHolder multiPartRequestHolder;
    private final XmlValidator xmlValidator;

    public void validate(AddAudio addAudio) {
        validateXml(addAudio);
        validateSize();
    }

    public void validate(Audio audio) {
        validateDuration(audio);
    }

    public void validateCourtroom(Audio audio) {
        //check courtroom in populated, if empty throw 500 to match legacy
        if (audio.getCourthouse() == null || audio.getCourthouse().isEmpty()) {
            throw new DartsException(CodeAndMessage.ERROR);
        }
    }

    private void validateXml(AddAudio audio) {
        Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();
        if (!request.isPresent()) {
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        if (validateAddAudio) {
            try {
                xmlValidator.validate(audio.getDocument(), addAudioSchema);
            } catch (DartsValidationException de) {
                log.info("Add Audio failed due to Invalid XML");
                throw new DartsValidationException(de, CodeAndMessage.INVALID_XML);
            }
        }
    }

    private void validateSize() {
        Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();
        if (request.isEmpty()) {
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }
        try {
            if (request.get().getBinarySize() > getBytes(expectedFileSize)) {
                log.info("Add Audio failed due to Audio too large");
                throw new DartsValidationException(CodeAndMessage.AUDIO_TOO_LARGE);
            }
        } catch (IOException | NullPointerException e) {
            log.info("Add Audio failed during size validation ");
            throw new DartsValidationException(e, CodeAndMessage.ERROR);
        }
    }

    private void validateDuration(Audio audio) {

        OffsetDateTime startDate = getAudioStartDateTime(audio);
        OffsetDateTime finishDate = getAudioEndDateTime(audio);

        Duration difference = Duration.between(startDate, finishDate);

        if (difference.compareTo(maxFileDuration) > 0) {
            log.info("Add Audio failed due to Duration too long");
            throw new DartsValidationException(CodeAndMessage.AUDIO_TOO_LARGE);
        }
    }

    public static long getBytes(long megaBytes) {
        return megaBytes * 1024 * 1024;
    }

}

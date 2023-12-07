package uk.gov.hmcts.darts.addaudio.validator;

import com.service.mojdarts.synapps.com.AddAudio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddAudioValidator {

    @Value("${darts-gateway.add-audio.schema}")
    private String addCaseSchemaPath;
    @Value("${darts-gateway.add-audio.validate}")
    private boolean validateAddAudio;
    @Value("${darts-gateway.add-audio.fileSizeInBytes}")
    private long expectedFileSize;

    private final XmlWithFileMultiPartRequestHolder multiPartRequestHolder;
    private final XmlValidator xmlValidator;

    public void validate(AddAudio audio) {
        validateXml(audio);
        validateSize();
    }

    private void validateXml(AddAudio audio) {
        Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();
        if (!request.isPresent()) {
            throw new DartsValidationException(null, CodeAndMessage.ERROR);
        }

        if (validateAddAudio) {
            try {
                xmlValidator.validate(audio.getDocument(), addCaseSchemaPath);
            } catch (DartsValidationException de) {
                throw new DartsValidationException(de, CodeAndMessage.INVALID_XML);
            }
        }
    }

    private void validateSize() {
        Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();
        if (!request.isPresent()) {
            throw new DartsValidationException(null, CodeAndMessage.ERROR);
        }
        try {
            if (request.get().getBinarySize() > expectedFileSize) {
                throw new DartsValidationException(null, CodeAndMessage.AUDIO_TOO_LARGE);
            }
        } catch (IOException ioe) {
            throw new DartsValidationException(ioe, CodeAndMessage.ERROR);
        }
    }
}

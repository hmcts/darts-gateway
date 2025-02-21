package uk.gov.hmcts.darts.addaudio.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.common.util.Validator;
import uk.gov.hmcts.darts.config.AllowedMediaConfig;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddAudioFileValidator implements Validator<MultipartFile> {

    @Autowired
    private final AllowedMediaConfig allowedMediaConfig;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    public void validate(MultipartFile addAudioFileRequest) {
        if (addAudioFileRequest.getSize() <= 0) {
            log.info("Add Audio failed due size too small");
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        if (addAudioFileRequest.getContentType() != null && !allowedMediaConfig.getAllowedMediaFormats().contains(addAudioFileRequest.getContentType())) {
            log.info("Add Audio failed due to invalid Content Type");
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        String extension = FilenameUtils.getExtension(addAudioFileRequest.getOriginalFilename());
        if (!allowedMediaConfig.getAllowedMediaExtensions().contains(extension)) {
            log.info("Add Audio failed due to invalid Extension. Got '{}' but expected one of '{}'", extension, allowedMediaConfig.getAllowedMediaExtensions());
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        // check the file signature is suitable
        try {
            Tika tika = new Tika();
            String mimeType
                = tika.detect(addAudioFileRequest.getInputStream());

            if (!allowedMediaConfig.getAllowedMediaMimeTypes().contains(mimeType)) {
                log.info("Add Audio failed due to invalid Signature");
                throw new DartsValidationException(CodeAndMessage.ERROR);
            }
        } catch (IOException ioException) {
            log.info("Add Audio failed during signature validation");
            throw new DartsValidationException(ioException, CodeAndMessage.ERROR);
        }
    }

}

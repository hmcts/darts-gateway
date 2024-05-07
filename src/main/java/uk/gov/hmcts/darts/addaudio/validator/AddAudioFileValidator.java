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

    public void validate(MultipartFile addAudioFileRequest) {
        if (addAudioFileRequest.getSize() <= 0) {
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        if (addAudioFileRequest.getContentType() != null && !isWhiteListedFileType(addAudioFileRequest.getContentType())) {
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        String extension = FilenameUtils.getExtension(addAudioFileRequest.getOriginalFilename());
        if (!isWhiteListedFileType(extension)) {
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }

        // check the file signature is suitable
        try {
            Tika tika = new Tika();
            String mimeType
                = tika.detect(addAudioFileRequest.getInputStream());

            if (!isWhiteListedFileType(mimeType)) {
                throw new DartsValidationException(CodeAndMessage.ERROR);
            }
        } catch (IOException ioException) {
            throw new DartsValidationException(CodeAndMessage.ERROR);
        }
    }

    public boolean isWhiteListedFileType(String type) {
        return allowedMediaConfig.getAllowedMediaFormats().stream().anyMatch(type::equals);
    }
}

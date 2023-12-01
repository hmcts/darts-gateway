package uk.gov.hmcts.darts.ws.multipart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.IOUtils;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Getter
@Setter
class MetaDataUploadPart {
    private final BodyPart xmlPart;
    private final BodyPart binaryPart;
    private File binaryFile;

    public boolean hasBinaryFile() {
        return binaryPart!=null;
    }

    public File getFileForBinary() throws MessagingException, IOException  {
        File payload = File.createTempFile("payload", ".tmp");

        IOUtils.copy(binaryPart.getInputStream(), Files.newOutputStream(Paths.get(payload.getPath())));

        return payload;
    }

    public void cleanup() {
        if (binaryFile != null) {
            binaryFile.delete();
        }
    }
}

package uk.gov.hmcts.darts.common.multipart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.mail.BodyPart;
import javax.mail.MessagingException;

@RequiredArgsConstructor
@Getter
class XmlFileUploadPart {
    private final BodyPart xmlPart;
    private final BodyPart binaryPart;
    private File binaryFile;

    private String xml;

    public String getXml()throws MessagingException, IOException {
        if (xml == null) {
            xml = IOUtils.toString(xmlPart.getInputStream(), Charset.defaultCharset());
        }

        return xml;
    }

    public boolean hasBinaryFile() {
        return binaryPart != null;
    }

    public InputStream getXmlStream() throws MessagingException, IOException {
        if (xml == null) {
            xml = IOUtils.toString(xmlPart.getInputStream(), Charset.defaultCharset());
        }

        return new ByteArrayInputStream(xml.getBytes());
    }

    public File getFileForBinary() throws MessagingException, IOException  {
        if (binaryFile == null) {
            File payload = File.createTempFile("payload", ".tmp");

            IOUtils.copy(binaryPart.getInputStream(), Files.newOutputStream(Paths.get(payload.getPath())));

            return payload;
        }

        return binaryFile;
    }

    public void cleanup() {
        if (binaryFile != null) {
            binaryFile.delete();
        }
    }
}

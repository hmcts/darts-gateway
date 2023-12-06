package uk.gov.hmcts.darts.common.multipart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.mail.BodyPart;
import javax.mail.MessagingException;

@RequiredArgsConstructor
@Getter
@Slf4j
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
            try (InputStream isStream = xmlPart.getInputStream()) {
                xml = IOUtils.toString(isStream, Charset.defaultCharset());
            }
        }

        return new ByteArrayInputStream(xml.getBytes());
    }

    public File getFileForBinary() throws MessagingException, IOException  {
        if (binaryFile == null) {
            File payload = File.createTempFile("payload", ".tmp", new File(System.getProperty("user.home")));
            binaryFile = payload;

            try (InputStream isStream = binaryPart.getInputStream();
                OutputStream osStream = Files.newOutputStream(Paths.get(payload.getPath()))) {
                IOUtils.copy(isStream, osStream);
            }

            return binaryFile;
        }

        return binaryFile;
    }

    public void cleanup() {
        if (binaryFile != null && !binaryFile.delete()) {
            log.error("Could not remove temporary binary file {} !!!. This will need manual removal", binaryFile.getAbsolutePath());
        }
    }
}

package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;


/**
 * A request that allows processing of incoming MTOM requests containing xml meta data and an optional binary file. This
 * implementation streams for the most part but does write a binary file where necessary to allow retries of uploaded byte data.
 *
 * <p>The implementation uses java mail as a basis of MTOM parsing. It assumes one piece of xml meta data per file upload at
 * the time of writing
 */
@Slf4j
public class JavaMailXmlWithFileMultiPartRequest extends HttpServletRequestWrapper implements XmlWithFileMultiPartRequest {
    private XmlFileUploadPart parsedData;


    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public JavaMailXmlWithFileMultiPartRequest(HttpServletRequest request) {
        super(request);
        parse(request);
    }

    private void parse(HttpServletRequest request) {
        try {
            MimeMultipart mimeMultipart = new MimeMultipart(new HttpRequestDataSource(request
            ));

            BodyPart xmlPayload = getXml(mimeMultipart);
            if (xmlPayload == null) {
                throw new DartsException(null, CodeAndMessage.ERROR);
            }

            BodyPart binary = getBinary(mimeMultipart);

            parsedData = new XmlFileUploadPart(xmlPayload, binary);
        } catch (MessagingException e) {
            log.error("Multipart parsing problem", e);
            throw new DartsException(e, CodeAndMessage.ERROR);
        }
    }

    private BodyPart getXml(MimeMultipart mimeMultipart) throws MessagingException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            String[] headers = mimeMultipart.getBodyPart(i).getHeader("Content-Type");
            if (headers.length > 0 && headers[0].contains("application/xop+xml")) {
                return mimeMultipart.getBodyPart(i);
            }
        }

        return null;
    }

    private BodyPart getBinary(MimeMultipart mimeMultipart) throws MessagingException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            String[] headers = mimeMultipart.getBodyPart(i).getHeader("Content-Type");
            if (headers.length > 0 && headers[0].contains("application/octet-stream")) {
                return mimeMultipart.getBodyPart(i);
            }
        }

        return null;
    }

    @Override
    public boolean consumeFileBinaryStream(ConsumerWithIoException<InputStream> fileInputStream) throws IOException {
        boolean processed = false;
        XmlFileUploadPart part = parsedData;
        if (part != null && part.hasBinaryFile()) {
            try (InputStream fileStream = Files.newInputStream(Path.of(parsedData.getFileForBinary().getAbsolutePath()))) {
                log.trace("Consuming binary file of payload");
                fileInputStream.accept(fileStream);
                log.trace("Consumed binary file of payload");
                processed = true;
            } catch (MessagingException ex) {
                throw new IOException(ex);
            }
        }

        return processed;
    }

    @Override
    public void consumeXmlBody(ConsumerWithIoException<InputStream> fileInputStream) throws IOException {
        XmlFileUploadPart part = parsedData;
        if (part != null && part.getXmlPart() != null) {
            try (InputStream xmlFileStream = parsedData.getXmlStream()) {
                log.trace("Consuming xml of payload");
                fileInputStream.accept(xmlFileStream);
                log.trace("Consumed xml of payload");
            } catch (MessagingException ex) {
                throw new IOException(ex);
            }
        }
    }

    @Override
    public boolean consumeFileBinary(ConsumerWithIoException<File> fileConsumer) throws IOException {
        boolean processed = false;

        if (parsedData.hasBinaryFile()) {
            try {
                File binaryFile = parsedData.getFileForBinary();
                log.trace("Consuming binary file of payload {}", binaryFile.getAbsolutePath());
                fileConsumer.accept(binaryFile);
                log.trace("Consumed binary file of payload {}", binaryFile.getAbsolutePath());
                processed = true;
            } catch (MessagingException | IOException e) {
                log.error("Problem consuming binary", e);
                throw new IOException(e);
            }
        }
        return processed;
    }

    @Override
    public String getHeader(String name) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            return "text/xml";
        }

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> values = super.getHeaders(name);
        String overrideValue = getHeader(name);

        if (overrideValue != null) {
            values = Collections.enumeration(List.of(overrideValue));
        }

        return values;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        try {
            String removedIncludeForxml = removeIncludeInXml();
            if (removedIncludeForxml != null) {
                return new BodyPartServletInputStream(removedIncludeForxml);
            } else {
                return new BodyPartServletInputStream(parsedData.getXmlPart());
            }
        } catch (MessagingException ex) {
            throw new IOException(ex);
        }
    }

    private String removeIncludeInXml() throws MessagingException, IOException {
        // added to ensure the xsd schema validation works. The include mtom portion is invalid according to the schema
        if (parsedData.hasBinaryFile()) {

            return parsedData.getXml().replaceAll("<Include.*\\/>", "");
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        parsedData.cleanup();
    }
}

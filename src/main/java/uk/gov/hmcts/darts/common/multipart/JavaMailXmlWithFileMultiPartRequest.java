package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
public class JavaMailXmlWithFileMultiPartRequest extends HttpServletRequestWrapper implements XmlWithFileMultiPartRequest, Closeable {
    private XmlFileUploadPart parsedData;

    private HttpRequestDataSource source;

    private static final int MAXIMUM_PARTS = 2;

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public JavaMailXmlWithFileMultiPartRequest(HttpServletRequest request) {
        super(request);
        parse(request);
    }

    private void parse(HttpServletRequest request) {
        try {
            source = new HttpRequestDataSource(request);
            MimeMultipart mimeMultipart = new MimeMultipart(source);

            if (mimeMultipart.getCount() > MAXIMUM_PARTS) {
                log.error("Error due to too many binary files");
                throw new DartsException(CodeAndMessage.ERROR);
            }

            BodyPart xmlPayload = MultiPartUtil.getXml(mimeMultipart);
            if (xmlPayload == null) {
                log.error("Error due to no XML being found for request");
                throw new DartsException((Throwable) null, CodeAndMessage.ERROR);
            }

            BodyPart binary = MultiPartUtil.getBinary(mimeMultipart);

            parsedData = new XmlFileUploadPart(xmlPayload, binary);
        } catch (IOException | MessagingException e) {
            log.error("Multipart parsing problem", e);
            throw new DartsException(e, CodeAndMessage.ERROR);
        }
    }



    @Override
    @SuppressWarnings({"squid:S2083"})
    public boolean consumeFileBinaryStream(ConsumerWithIoException<SizeableInputSource> fileInputStream) throws IOException {
        boolean processed = false;
        XmlFileUploadPart part = parsedData;
        if (part != null && part.hasBinaryFile()) {
            try {
                try (InputStream fileStream = parsedData.getBinaryPart().getInputStream()) {
                    log.trace("Consuming binary file of payload");
                    fileInputStream.accept(new SizeableInputSource() {
                        @Override
                        public long getSize() throws IOException {
                            try {
                                return parsedData.getBinaryPart().getSize();
                            } catch (MessagingException me) {
                                throw new IOException(me);
                            }
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return fileStream;
                        }
                    });
                    log.trace("Consumed binary file of payload");
                    processed = true;
                }
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
    public long getBinarySize() throws IOException {
        if (parsedData != null) {
            try {
                return parsedData.getFileForBinary().length();
            } catch (MessagingException | IOException e) {
                log.error("Problem consuming binary", e);
                throw new IOException(e);
            }
        }

        throw new IOException("File does not exist");
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
        source.close();
    }
}

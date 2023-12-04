package uk.gov.hmcts.darts.ws.multipart;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.xml.soap.*;
import org.apache.commons.io.IOUtils;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;


/**
 * A request that allows processing of incoming MTOM requests containing meta data and an optional binary file. This
 * implementation streams for the most part but does write a binary file where necessary to allow retries of upload byte data.
 *
 * The implementation uses java mail as a basis of MTOM parsing. It assumes one piece of meta data per file upload at
 * the time of writing
 */
public class DartsMetaDataMultiPartHttpUploadRequest extends HttpServletRequestWrapper implements MTOMMetaDataAndUploadRequest, Closeable {
    private MetaDataUploadPart parsedData;

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public DartsMetaDataMultiPartHttpUploadRequest(HttpServletRequest request) {
        super(request);
        try {
            parse(request);
        } catch (Exception e) {
            throw new DartsException(e, CodeAndMessage.ERROR);
        }
    }

    private void parse(HttpServletRequest request) {
        try {
            MimeMultipart mimeMultipart = new MimeMultipart(new HttpRequestDataSource(request
            ));

            BodyPart xml = getXML(mimeMultipart);
            BodyPart binary = getBinary(mimeMultipart);

            parsedData = new MetaDataUploadPart(xml, binary);
        } catch (MessagingException e) {
            throw new DartsException(e, CodeAndMessage.ERROR);
        }
    }

    private BodyPart getXML(MimeMultipart mimeMultipart) throws MessagingException {
        for (int i=0; i < mimeMultipart.getCount(); i++) {
            String[] headers = mimeMultipart.getBodyPart(i).getHeader("Content-Type");
            if (headers.length > 0) {
                if (headers[0].contains("application/xop+xml")) {
                    return mimeMultipart.getBodyPart(i);
                }
            }
        }

        return null;
    }

    private BodyPart getBinary(MimeMultipart mimeMultipart) throws MessagingException {
        for (int i=0; i < mimeMultipart.getCount(); i++) {
            String[] headers = mimeMultipart.getBodyPart(i).getHeader("Content-Type");
            if (headers.length > 0) {
                if (headers[0].contains("application/octet-stream")) {
                    return mimeMultipart.getBodyPart(i);
                }
            }
        }

        return null;
    }

    private String getMTOMBinaryBoundaryId(BodyPart mimeMultipart) throws MessagingException {
        String[] idheaders = mimeMultipart.getHeader("Content-Id");
        if (idheaders.length > 0) {
            return idheaders[0];
        }
        return null;
    }

    @Override
    public boolean consumeFileBinaryStream(ConsumerWithIOException<InputStream> fileInputStream) throws IOException {
        boolean processed = false;
        MetaDataUploadPart part = parsedData;
        if (part != null && part.hasBinaryFile()) {
            try {
                fileInputStream.accept(new FileInputStream(parsedData.getFileForBinary()));
                processed = true;
            } catch (MessagingException ex) {
                throw new IOException(ex);
            }
        }

        return processed;
    }

    @Override
    public boolean consumeSOAPXML(ConsumerWithIOException<InputStream> fileInputStream) throws IOException {
        MetaDataUploadPart part = parsedData;
        if (part != null && part.getXmlPart()!=null) {
            try {
                fileInputStream.accept(parsedData.getXMLStream());
            }
            catch (MessagingException ex) {
                throw new IOException(ex);
            }
        }

        return part != null;
    }

    @Override
    public boolean consumeFileBinary(ConsumerWithIOException<File> fileConsumer) throws IOException {
        boolean processed = false;

        if (parsedData.hasBinaryFile()) {
            try {
                File binaryFile = parsedData.getFileForBinary();
                fileConsumer.accept(binaryFile);
                processed = true;
            }
            catch (MessagingException | IOException e) {
                throw new IOException(e);
            }
        }
        return processed;
    }

    @Override
    public String getHeader(String name) {
        if (name.equalsIgnoreCase("Content-Type")) {
            return "text/xml";
        }

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> values = super.getHeaders(name);
        String override = getHeader(name);

        if (override!=null){
            values = Collections.enumeration(List.of(override));
        }

        return values;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        try {
            String removedIncludeForxml = removeIncludeInXML();
            if (removedIncludeForxml != null) {
                return new BodyPartServletInputStream(removedIncludeForxml);
            }
            else {
                return new BodyPartServletInputStream(parsedData.getXmlPart());
            }
        }
        catch (MessagingException ex) {
            throw new IOException(ex);
        }
    }

    private String removeIncludeInXML() throws MessagingException, IOException {
        // added to ensure the xsd schema validation works. The include mtom portion is invalid according to the schema
        if (parsedData.hasBinaryFile()) {
            String includeId = getMTOMBinaryBoundaryId(parsedData.getBinaryPart()).replace("<", "").replace(">", "");
            return parsedData.getXml().replace(String.format("<Include xmlns=\"http://www.w3.org/2004/08/xop/include\" href=\"cid:%1s\"/>", includeId), "");
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        parsedData.cleanup();
    }
}

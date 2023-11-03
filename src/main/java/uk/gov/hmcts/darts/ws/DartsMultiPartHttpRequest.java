package uk.gov.hmcts.darts.ws;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;


public class DartsMultiPartHttpRequest extends HttpServletRequestWrapper {

    private static final int MEDIA_PLUS_XML_PART_COUNT = 2;

    private InputStream mediaStream;

    private InputStream soapStream;

    private File payload;


    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public DartsMultiPartHttpRequest(HttpServletRequest request) {
        super(request);

        try {
            // write the payload to a file as we need it post parsing
            payload = File.createTempFile("payload", ".tmp");

            IOUtils.copy(request.getInputStream(), Files.newOutputStream(Paths.get(payload.getPath())));
            MimeMultipart mimeMultipart = new MimeMultipart(new ByteArrayDataSource(
                Files.newInputStream(Paths.get(payload.getPath())),
                "application/octet-stream"
            ));
            int count = mimeMultipart.getCount();

            // we need to check for both xml and binary. This will not work if we have multiple binary
            // payloads to upload
            if (count == MEDIA_PLUS_XML_PART_COUNT) {
                int mediaPartIndex = 1;
                if (getBinary(mimeMultipart.getBodyPart(0))) {
                    mediaPartIndex = 0;
                }

                int soapPart = mediaPartIndex == 0 ? 1 : 0;
                mediaStream = mimeMultipart.getBodyPart(mediaPartIndex).getInputStream();
                soapStream = mimeMultipart.getBodyPart(soapPart).getInputStream();

                request.getSession().setAttribute("mediastream", mediaStream);
            } else if (count == 1) {
                soapStream = mimeMultipart.getBodyPart(0).getInputStream();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getBinary(BodyPart part) throws IOException {
        try {
            return "application/octet-stream".equals(part.getHeader("Content-Type")[0]);
        } catch (MessagingException me) {
            throw new IOException(me);
        }
    }

    public boolean isMultipart() throws IOException {
        return soapStream != null && soapStream.available() > 0;
    }

    public InputStream mediaStream() {
        // allow the payload to return the media steam input stream
        return (InputStream) ((HttpServletRequest) getRequest()).getSession().getAttribute("mediastream");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // re read the original payload
        CachedServletInputStream stream = new CachedServletInputStream(Files.newInputStream(payload.toPath()));
        payload.delete();
        return stream;
    }

    class CachedServletInputStream extends ServletInputStream {
        private final InputStream input;

        public CachedServletInputStream(InputStream bytes) {
            super();
            input = bytes;
        }

        @Override
        public int read() throws IOException {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return input.available() > 0;
            } catch (IOException io) {
                return false;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
    }
}

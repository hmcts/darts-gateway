package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.mail.BodyPart;
import javax.mail.MessagingException;

class BodyPartServletInputStream extends ServletInputStream {
    private final InputStream input;

    public BodyPartServletInputStream(BodyPart payload) throws MessagingException, IOException {
        super();

        try (InputStream is = payload.getInputStream()) {
            input = new ByteArrayInputStream(IOUtils.toString(is, Charset.defaultCharset()).getBytes());
        }
    }

    public BodyPartServletInputStream(String payload) throws MessagingException, IOException {
        super();

        input = new ByteArrayInputStream(payload.getBytes());
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

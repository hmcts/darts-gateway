package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.BodyPart;
import javax.mail.MessagingException;

class BodyPartServletInputStream extends ServletInputStream {
    private final InputStream input;

    public BodyPartServletInputStream(BodyPart payload) throws MessagingException, IOException {
        super();

        input = payload.getInputStream();
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

package uk.gov.hmcts.darts.ws.multipart;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import java.io.*;

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

    @Override
    public void close() throws IOException {
        super.close();

        //file.delete();
    }
}

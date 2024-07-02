package uk.gov.hmcts.darts.common.multipart;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.activation.DataSource;
import javax.mail.util.SharedFileInputStream;

@Slf4j
public class HttpRequestDataSource implements DataSource, Closeable {

    private final HttpServletRequest request;

    private final File payload;

    private final SharedFileInputStream sharedFileInputStream;

    public HttpRequestDataSource(HttpServletRequest request) throws IOException {
        this.request = request;

        payload = File.createTempFile("payload", ".tmp", new File(System.getProperty("user.home")));

        try (InputStream isStream = request.getInputStream();
            OutputStream osStream = Files.newOutputStream(Paths.get(payload.getPath()))) {
            IOUtils.copy(isStream, osStream);
        }

        sharedFileInputStream = new NonCloseableSharedFileStream(payload);
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return new NonCloseableSharedFileStream(payload);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void close() throws IOException {
        // clear down the main parsed content file
        if (payload != null && !payload.delete()) {
            log.error("Could not remove temporary binary file {} !!!. This will need manual removal", payload.getAbsolutePath());
        }
    }

    class NonCloseableSharedFileStream extends SharedFileInputStream {

        @SuppressWarnings("javasecurity:S2083")
        public NonCloseableSharedFileStream(File file) throws IOException {
            super(file);
        }

        @Override
        public void close() throws IOException {

            // DO NOTHING JAVA MAIL CLOSES ALL PART STREAMS  HERE WHICH IS WRONG WHEN WORKING WITH MULTIPARTS
        }
    }
}

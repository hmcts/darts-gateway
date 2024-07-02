package uk.gov.hmcts.darts.common.multipart;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import uk.gov.hmcts.darts.common.client.multipart.StreamingMultipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

class DefaultMultipartTest {

    private final StreamingMultipart multipart;

    private static final String NAME = "TESTNAME";

    private static final String CONTENT_TYPE = "CONTENT_TYPE";

    private static final String IS_BYTES = "test";

    static SizeableInputSource getIS(String contents) {
        return new SizeableInputSource() {
            @Override
            public long getSize() {
                return contents.length();
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(contents.getBytes());
            }
        };
    }

    DefaultMultipartTest() throws IOException {
        multipart = new StreamingMultipart(NAME, CONTENT_TYPE, getIS(IS_BYTES));
    }

    @Test
    void testInputStream() throws Exception {
        InputStream stream = multipart.getInputStream();
        Assertions.assertEquals(IS_BYTES, IOUtils.toString(stream, Charset.defaultCharset()));
    }

    @Test
    void testInputStreamFailOnSecondCall() throws Exception {
        InputStream stream = multipart.getInputStream();
        Assertions.assertEquals(IS_BYTES.getBytes().length, multipart.getInputStream().available());
        Assertions.assertEquals(IS_BYTES, IOUtils.toString(stream, Charset.defaultCharset()));
        Assertions.assertEquals(0L, multipart.getInputStream().available());
    }

    @Test
    void testGetName() throws Exception {
        Assertions.assertEquals(NAME, multipart.getName());
    }

    @Test
    void testGetContentType() throws Exception {
        Assertions.assertEquals(CONTENT_TYPE, multipart.getContentType());
    }

    @Test
    void testGetOriginalName() throws Exception {
        Assertions.assertEquals(NAME, multipart.getOriginalFilename());
    }

    @Test
    void testIsEmptyFalse() throws Exception {
        Assertions.assertFalse(multipart.isEmpty());
    }

    @Test
    void testIsEmptyTrue() throws Exception {
        StreamingMultipart streamingMultipart = new StreamingMultipart(NAME, CONTENT_TYPE, getIS(""));
        Assertions.assertTrue(streamingMultipart.isEmpty());
    }

    @Test
    void testTransferTo() throws Exception {
        File payloadFile = null;
        try {
            payloadFile = File.createTempFile("payload", ".tmp",
                    new File(System.getProperty("user.home")));
            Assertions.assertEquals(0, payloadFile.length());
            multipart.transferTo(payloadFile);
            Assertions.assertTrue(payloadFile.length() > 0);
        } finally {
            if (payloadFile != null) {
                payloadFile.delete();
            }
        }
    }

    @Test
    void testGetContentsOFile() {
        Assertions.assertThrows(UnsupportedOperationException.class, multipart::getBytes);
    }
}

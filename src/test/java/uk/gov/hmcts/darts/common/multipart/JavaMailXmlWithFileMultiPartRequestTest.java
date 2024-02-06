package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

class JavaMailXmlWithFileMultiPartRequestTest {
    private static final String BINARY_PLACEHOLDER = "${BINARY CONTENT}";

    private static final String XML_PLACEHOLDER = "${XML CONTENT}";

    private JavaMailXmlWithFileMultiPartRequest requestUnderTest;

    @AfterEach
    void afterTest() throws IOException {
        if (requestUnderTest != null) {
            requestUnderTest.close();
        }
    }

    @Test
    void testXmlAndBinaryLoad() throws Exception {
        File multipartFile = new File(JavaMailXmlWithFileMultiPartRequestTest
                                          .class.getClassLoader().getResource("tests/multipart/httpmultipartuploadsuccess.txt").getFile());
        File payloadFileUploadContents = new File(
            JavaMailXmlWithFileMultiPartRequestTest.class.getClassLoader().getResource("tests/multipart/file.txt").getFile());
        File payloadXmlContents = new File(JavaMailXmlWithFileMultiPartRequestTest.class.getClassLoader().getResource("tests/multipart/payload.xml").getFile());
        String payloadBody = IOUtils.toString(Files.newInputStream(Paths.get(multipartFile.getAbsolutePath())));
        String fileContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadFileUploadContents.getAbsolutePath())));
        String xmlContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadXmlContents.getAbsolutePath())));
        DefaultInputServletStream servletFileStream = new DefaultInputServletStream(payloadBody
            .replace(BINARY_PLACEHOLDER, fileContents)
            .replace(XML_PLACEHOLDER, xmlContents)
            .getBytes("UTF-8"));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(servletFileStream);

        // run the test
        requestUnderTest = new JavaMailXmlWithFileMultiPartRequest(request);

        // make the assertions
        requestUnderTest.consumeXmlBody(is -> {
            Assertions.assertEquals(IOUtils.toString(xmlContents.getBytes()), IOUtils.toString(is.readAllBytes())); });
        Assertions.assertTrue(requestUnderTest.consumeFileBinaryStream(is -> {
            Assertions.assertEquals(IOUtils.toString(fileContents.getBytes()), IOUtils.toString(is.getInputStream().readAllBytes())); }));
        Assertions.assertTrue(requestUnderTest.consumeFileBinary(file -> {
            Assertions.assertEquals(IOUtils.toString(fileContents.getBytes()), IOUtils.toString(
                    Files.newInputStream(Paths.get(file.getAbsolutePath())))); }));
    }

    @Test
    void testNoBinaryExisting() throws Exception {
        File multipartFile = new File(JavaMailXmlWithFileMultiPartRequestTest
                                          .class.getClassLoader().getResource("tests/multipart/httpmultipartuploadsuccessnobinary.txt").getFile());
        File payloadXmlContents = new File(JavaMailXmlWithFileMultiPartRequestTest
                                               .class.getClassLoader().getResource("tests/multipart/payload.xml").getFile());
        String payloadBody =  IOUtils.toString(Files.newInputStream(Paths.get(multipartFile.getAbsolutePath())));
        String xmlContents =  IOUtils.toString(Files.newInputStream(Paths.get(payloadXmlContents.getAbsolutePath())));
        DefaultInputServletStream servletFileStream = new DefaultInputServletStream(payloadBody
                                                                                        .replace(XML_PLACEHOLDER, xmlContents)
                                                                                        .getBytes("UTF-8"));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(servletFileStream);

        // run the test
        requestUnderTest = new JavaMailXmlWithFileMultiPartRequest(request);
        requestUnderTest.consumeXmlBody(is -> {
            Assertions.assertEquals(IOUtils.toString(xmlContents.getBytes()), new String(is.readAllBytes())); });
        Assertions.assertFalse(requestUnderTest.consumeFileBinaryStream(is -> { }));
    }

    @Test
    void testNoXmlExistingFailureAndDoesNotFail() throws Exception {
        File multipartFile = new File(JavaMailXmlWithFileMultiPartRequestTest
                                          .class.getClassLoader().getResource("tests/multipart/httpmultipartuploadfailnoxml.txt").getFile());
        File payloadFileUploadContents = new File(JavaMailXmlWithFileMultiPartRequestTest
                                                      .class.getClassLoader().getResource("tests/multipart/file.txt").getFile());
        String payloadBody = IOUtils.toString(Files.newInputStream(Paths.get(multipartFile.getAbsolutePath())));
        String fileContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadFileUploadContents.getAbsolutePath())));
        DefaultInputServletStream servletFileStream = new DefaultInputServletStream(payloadBody
                                                                                        .replace(BINARY_PLACEHOLDER, fileContents)
                                                                                        .getBytes("UTF-8"));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(servletFileStream);

        new JavaMailXmlWithFileMultiPartRequest(request);
    }

    @Test
    void testParseWithMultipleBinaryPartsAndDoesNotFail() throws Exception {
        File multipartFile = new File(JavaMailXmlWithFileMultiPartRequestTest
                .class.getClassLoader().getResource("tests/multipart/httpmultipartuploadmorethanonebinary.txt").getFile());
        File payloadFileUploadContents = new File(
                JavaMailXmlWithFileMultiPartRequestTest.class.getClassLoader().getResource("tests/multipart/file.txt").getFile());
        File payloadXmlContents = new File(JavaMailXmlWithFileMultiPartRequestTest.class.getClassLoader().getResource("tests/multipart/payload.xml").getFile());
        String payloadBody = IOUtils.toString(Files.newInputStream(Paths.get(multipartFile.getAbsolutePath())));
        String fileContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadFileUploadContents.getAbsolutePath())));
        String xmlContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadXmlContents.getAbsolutePath())));
        DefaultInputServletStream servletFileStream = new DefaultInputServletStream(payloadBody
                .replace(BINARY_PLACEHOLDER, fileContents)
                .replace(XML_PLACEHOLDER, xmlContents)
                .getBytes("UTF-8"));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(servletFileStream);

        // run the test
        new JavaMailXmlWithFileMultiPartRequest(request);
    }

    @Test
    void testParseWithMultipleBinaryPartMisFormattedAndDoesNotFail() throws Exception {
        File multipartFile = new File(JavaMailXmlWithFileMultiPartRequestTest
                .class.getClassLoader().getResource("tests/multipart/httpmultipartuploadmisformatted.txt").getFile());
        File payloadFileUploadContents = new File(
                JavaMailXmlWithFileMultiPartRequestTest.class.getClassLoader().getResource("tests/multipart/file.txt").getFile());
        File payloadXmlContents = new File(JavaMailXmlWithFileMultiPartRequestTest.class.getClassLoader().getResource("tests/multipart/payload.xml").getFile());
        String payloadBody = IOUtils.toString(Files.newInputStream(Paths.get(multipartFile.getAbsolutePath())));
        String fileContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadFileUploadContents.getAbsolutePath())));
        String xmlContents = IOUtils.toString(Files.newInputStream(Paths.get(payloadXmlContents.getAbsolutePath())));
        DefaultInputServletStream servletFileStream = new DefaultInputServletStream(payloadBody
                .replace(BINARY_PLACEHOLDER, fileContents)
                .replace(XML_PLACEHOLDER, xmlContents)
                .getBytes("UTF-8"));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getInputStream()).thenReturn(servletFileStream);

        // run the test
        new JavaMailXmlWithFileMultiPartRequest(request);
    }

    class DefaultInputServletStream extends ServletInputStream {
        private int lastIndexRetrieved = -1;

        private final byte[] contents;

        public DefaultInputServletStream(byte[] contents) {
            super();
            this.contents = Arrays.copyOf(contents, contents.length);
        }

        @Override
        public boolean isFinished() {
            return lastIndexRetrieved == contents.length - 1;
        }

        @Override
        public boolean isReady() {
            // This implementation will never block
            // We also never need to call the readListener from this method, as this method will never return false
            return isFinished();
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            int data;
            if (isFinished()) {
                return - 1;
            } else {
                data = contents[lastIndexRetrieved + 1];
                lastIndexRetrieved++;
                return data;
            }
        }
    }
}

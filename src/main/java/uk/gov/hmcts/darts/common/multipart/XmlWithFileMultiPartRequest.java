package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.common.function.ConsumerWithIoException;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * <p/>Implements an MTOM MetaData HTTP request. The implementation parses associated meta data so that the
 * boundary contents can be queried at a later date
 * <p/>NOTE: Any implementation needs to clean up after itself i.e. if writing files to the file system there
 * should be a mechanisms to clean up this data.
 */
public interface XmlWithFileMultiPartRequest extends HttpServletRequest, Closeable {
    static Optional<XmlWithFileMultiPartRequest> getCurrentRequest() {

        HttpServletRequest curRequest =
            ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest();

        while (curRequest instanceof HttpServletRequestWrapper) {
            if (curRequest instanceof XmlWithFileMultiPartRequest) {
                return Optional.of((XmlWithFileMultiPartRequest) curRequest);
            } else {
                curRequest = (HttpServletRequest)((HttpServletRequestWrapper) curRequest).getRequest();
            }
        }

        return Optional.empty();
    }

    static boolean isMultipart(HttpServletRequest request) throws IOException {
        return request.getHeader("Content-Type").contains("multipart/related")
            && request.getHeader("Content-Type").contains("boundary=");
    }

    /**
     * Read a file. If false there was no binary to process
     * @return A boolean signifying success
     * @throws IOException Any IO issues
     * */
    boolean consumeFileBinary(ConsumerWithIoException<File> file) throws IOException;

    /**
     * Read a file stream associated with the binary. The caller need not close the stream this is handled for you
     * @return A boolean signifying success. If false there was no binary to process
     * @throws IOException Any IO issues
     */
    boolean consumeFileBinaryStream(ConsumerWithIoException<SizeableInputSource> fileInputStream) throws IOException;

    /**
     * Read the xml. The caller need not close the stream this is handled for you
     * @throws IOException Any IO issues
     */
    void consumeXmlBody(ConsumerWithIoException<InputStream> fileInputStream) throws IOException;

    /**
     * get the size in bytes of the underlying binary.
     * @return The bytes available
     * @throws IOException If the file does not exist
     */
    long getBinarySize() throws IOException;
}

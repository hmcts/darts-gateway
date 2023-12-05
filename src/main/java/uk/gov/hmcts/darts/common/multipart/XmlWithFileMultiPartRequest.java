package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.http.HttpServletRequest;
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

        if (curRequest instanceof XmlWithFileMultiPartRequest) {
            return Optional.ofNullable((XmlWithFileMultiPartRequest) curRequest);
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
     * */
    boolean consumeFileBinary(ConsumerWithIoException<File> file) throws IOException;

    /**
     * Read a file stream associated with the binary. The caller need not close the stream this is handled for you
     * @return A boolean signifying success. If false there was no binary to process
     */
    boolean consumeFileBinaryStream(ConsumerWithIoException<InputStream> fileInputStream) throws IOException;

    /**
     * Read the xml. The caller need not close the stream this is handled for you
     */
    void consumeXmlBody(ConsumerWithIoException<InputStream> fileInputStream) throws IOException;
}

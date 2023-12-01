package uk.gov.hmcts.darts.ws.multipart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implements an MTOM MetaData HTTP request. The implementation parses associated meta data so that the
 * boundary contents can be queried at a later date
 *
 * NOTE: Any implementation needs to clean up after itself i.e. if writing files to the file system there
 * should be a mechanisms to clean up this data.
 */
public interface MTOMMetaDataAndUploadRequest extends HttpServletRequest{
    static Optional<MTOMMetaDataAndUploadRequest> getCurrentRequest() {

        HttpServletRequest curRequest =
            ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest();

        if (curRequest instanceof MTOMMetaDataAndUploadRequest) {
            return Optional.ofNullable((MTOMMetaDataAndUploadRequest) curRequest);
        }

        return Optional.empty();
    }

    static boolean isMultipart(HttpServletRequest request) throws IOException {
        return request.getHeader("Content-Type").contains("multipart/related") &&
            request.getHeader("Content-Type").contains("boundary=");
    }

    /**
     * Read a file associated with the boundary.  The consumer could get called multiple times if there are many files per boundary
     * i.e. removed from the file system
     * @return A boolean signifying success
     * */
    boolean consumeFileBinary(Consumer<File> fileInputStream) throws IOException;

    /**
     * Read a file associated with the boundary. The caller need not close the stream this is handler for you
     * NOTE: The binary file is cleaned up after this call. Any subsequent calls will fail.
     * The consumer could get called multiple times if there are many files per boundary
     * @return A boolean signifying success
     */
    boolean consumeFileBinaryStream(Consumer<InputStream> fileInputStream) throws IOException;

    /**
     * Read the soap xml associated with the boundary NOTE: This will only be called once
     * @return A boolean signifying success
     */
    boolean consumeSOAPXML(Consumer<InputStream> fileInputStream) throws IOException;
}

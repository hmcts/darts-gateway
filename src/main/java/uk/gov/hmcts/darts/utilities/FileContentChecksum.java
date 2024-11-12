package uk.gov.hmcts.darts.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.codec.digest.DigestUtils.md5;

/**
 * Calculates the file checksum equivalent to "md5 filename" on the command line.
 * <p>
 * This is not the same as the Azure Blob Storage CONTENT-MD5 tag value as a base64 encoded
 * representation of the binary MD5 hash value. This could be done in Bash e.g. openssl md5 -binary "Test
 * Document.doc" | base64
 * </p>
 */
@Component
@Slf4j
public final class FileContentChecksum {

    private FileContentChecksum() {

    }

    public static String calculate(InputStream inputStream) throws IOException {
        return encodeToString(md5(inputStream));
    }

    public static String encodeToString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }

}

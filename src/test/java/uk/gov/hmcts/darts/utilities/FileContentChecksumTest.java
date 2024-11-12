package uk.gov.hmcts.darts.utilities;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FileContentChecksumTest {

    private static final byte[] TEST_DATA = "test".getBytes(StandardCharsets.UTF_8);
    private static final String EXPECTED_STRING_MD5_CHECKSUM = "098f6bcd4621d373cade4e832627b4f6";

    @Test
    void calculateFromInputStream() throws IOException {
        ByteArrayInputStream testDataInputStream = new ByteArrayInputStream(TEST_DATA);
        String calculatedChecksum = FileContentChecksum.calculate(testDataInputStream);
        assertThat(calculatedChecksum).isEqualTo(EXPECTED_STRING_MD5_CHECKSUM);
    }
}

package uk.gov.hmcts.darts.common.multipart;

import org.springframework.core.io.InputStreamSource;

import java.io.IOException;

public interface SizeableInputSource extends InputStreamSource {
    long getSize() throws IOException;
}

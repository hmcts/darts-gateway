package uk.gov.hmcts.darts.common.multipart;

import org.springframework.core.io.InputStreamSource;

public interface SizeableInputSource extends InputStreamSource {
    long getSize();
}

package uk.gov.hmcts.darts.common.multipart;

import org.springframework.core.io.InputStreamSource;

public interface SizesableInputSource extends InputStreamSource {
    long getSize();
}

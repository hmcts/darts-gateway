package uk.gov.hmcts.darts.common.multipart;

import java.util.Optional;

public interface XmlWithFileMultiPartRequestHolder {
    default Optional<XmlWithFileMultiPartRequest> getRequest() {
        return XmlWithFileMultiPartRequest.getCurrentRequest();
    }
}

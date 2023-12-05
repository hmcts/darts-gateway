package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.http.HttpServletRequest;

public interface XmlAndFileUploadRequestFactory {
    XmlWithFileMultiPartRequest getRequest(HttpServletRequest request);
}

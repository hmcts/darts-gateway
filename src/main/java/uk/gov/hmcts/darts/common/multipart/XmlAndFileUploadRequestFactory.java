package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface XmlAndFileUploadRequestFactory {
    XmlWithFileMultiPartRequest getRequest(HttpServletRequest request);
}

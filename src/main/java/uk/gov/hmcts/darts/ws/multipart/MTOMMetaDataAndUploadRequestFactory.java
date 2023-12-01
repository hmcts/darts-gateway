package uk.gov.hmcts.darts.ws.multipart;

import jakarta.servlet.http.HttpServletRequest;

public interface MTOMMetaDataAndUploadRequestFactory {
    MTOMMetaDataAndUploadRequest getRequest(HttpServletRequest request);
}

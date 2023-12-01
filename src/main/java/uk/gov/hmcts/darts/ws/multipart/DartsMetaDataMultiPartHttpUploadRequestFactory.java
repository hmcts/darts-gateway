package uk.gov.hmcts.darts.ws.multipart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class DartsMetaDataMultiPartHttpUploadRequestFactory implements MTOMMetaDataAndUploadRequestFactory {
    @Override
    public MTOMMetaDataAndUploadRequest getRequest(HttpServletRequest request) {
        return new DartsMetaDataMultiPartHttpUploadRequest(request);
    }
}

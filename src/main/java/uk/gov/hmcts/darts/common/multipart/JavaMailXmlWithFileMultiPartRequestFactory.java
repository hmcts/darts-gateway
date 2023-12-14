package uk.gov.hmcts.darts.common.multipart;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JavaMailXmlWithFileMultiPartRequestFactory implements XmlAndFileUploadRequestFactory {
    @Override
    public XmlWithFileMultiPartRequest getRequest(HttpServletRequest request) {
        return new JavaMailXmlWithFileMultiPartRequest(request);
    }
}

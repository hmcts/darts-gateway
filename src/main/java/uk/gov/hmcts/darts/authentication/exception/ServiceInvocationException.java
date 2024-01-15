package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(
    name = "ServiceInvocationException",
    namespace = "http://rt.fs.documentum.emc.com/"
)
public class ServiceInvocationException extends ServiceException {
    public ServiceInvocationException(String errorCode, String... args) {
        super(errorCode, args);
    }
}

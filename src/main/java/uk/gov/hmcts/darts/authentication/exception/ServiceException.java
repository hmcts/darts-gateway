package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.ws.WebFault;
import lombok.Getter;
import lombok.Setter;

@WebFault(
    faultBean = "com.emc.documentum.fs.rt.ServiceException_Bean",
    name = "ServiceException",
    targetNamespace = "http://rt.fs.documentum.emc.com/"
)
@XmlType(
    name = "ServiceException",
    namespace = "http://rt.fs.documentum.emc.com/"
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ServiceException extends Exception {
    private String messageId;
    private Object[] messageArgs;

    public ServiceException() {
    }

    public ServiceException(String code, String message) {
        super(message);
        messageId = code;
        messageArgs = new Object[] {};
    }

}

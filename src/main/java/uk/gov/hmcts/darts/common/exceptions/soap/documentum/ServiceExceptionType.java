package uk.gov.hmcts.darts.common.exceptions.soap.documentum;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

import java.util.ResourceBundle;

@XmlType(
    name = "ServiceInvocationException",
    namespace = "http://rt.fs.documentum.emc.com/"
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@XmlRootElement
public class ServiceExceptionType {
    private String messageId;
    private String message;
    private String[] messageArgs;
    private String exceptionType;

    private static ResourceBundle BUNDLE = ResourceBundle.getBundle("SoapExceptionMessages");

    public ServiceExceptionType(String code, String exceptionType, String... args) {
        messageId = code;
        messageArgs = args;
        message = SoapFaultServiceException.getMessage(code, args);
        this.exceptionType = exceptionType;
    }

    public ServiceExceptionType() {

    }
}

package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.ws.WebFault;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

import java.text.MessageFormat;
import java.util.ResourceBundle;

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
    private String exceptionType;

    private static ResourceBundle BUNDLE = ResourceBundle.getBundle("SoapExceptionMessages");

    public ServiceException() {
    }

    public ServiceException(String code, String... args) {
        super(getMessage(code, args));
        messageId = code;
        messageArgs = args;
    }

    private static String getMessage(String key, String... args) {
        return MessageFormat.format(BUNDLE.getString(key), args);
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }
}

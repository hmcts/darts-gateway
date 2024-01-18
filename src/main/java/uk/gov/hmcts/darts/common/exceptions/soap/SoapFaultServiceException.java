package uk.gov.hmcts.darts.common.exceptions.soap;

import lombok.Getter;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@Getter
public class SoapFaultServiceException extends RuntimeException {
    private ServiceExceptionType serviceExceptionType;

    private static ResourceBundle BUNDLE = ResourceBundle.getBundle("SoapExceptionMessages");

    public SoapFaultServiceException() {
    }

    public SoapFaultServiceException(FaultErrorCodes code, String type, String... args) {
        super(getMessage(code.name(), args));
        serviceExceptionType = new ServiceExceptionType(code.name(), type, args);
    }

    public SoapFaultServiceException(FaultErrorCodes code, String... args) {
        super(getMessage(code.name(), args));
        serviceExceptionType = new ServiceExceptionType(code.name(), "", args);
    }

    public SoapFaultServiceException(FaultErrorCodes code) {
        super(getMessage(code.name(), ""));
        serviceExceptionType = new ServiceExceptionType(code.name(), "", "");
    }

    public static String getMessage(String key, String... args) {
        return MessageFormat.format(BUNDLE.getString(key), (Object[])args);
    }
}

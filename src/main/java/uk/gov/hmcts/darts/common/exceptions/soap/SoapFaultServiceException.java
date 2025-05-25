package uk.gov.hmcts.darts.common.exceptions.soap;

import lombok.Getter;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@Getter
public class SoapFaultServiceException extends RuntimeException {
    private ServiceExceptionType serviceExceptionType;

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("SoapExceptionMessages");

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public SoapFaultServiceException() {
        //Empty constructor
    }

    public SoapFaultServiceException(FaultErrorCodes code, Throwable cause, String arg) {
        super(getMessage(code.name(), arg), cause);
        serviceExceptionType = new ServiceExceptionType(code.name(), this, arg);

        if (cause == null) {
            return;
        }

        if (!(cause instanceof SoapFaultServiceException)) {
            serviceExceptionType.addHolderCause(getMessage(FaultErrorCodes.E_UNKNOWN_TOKEN.name(), arg),
                                                FaultErrorCodes.E_UNKNOWN_TOKEN.name(), cause, arg
            );
        } else {
            serviceExceptionType.addHolderCause(((SoapFaultServiceException) cause).getServiceExceptionType().getMessage(),
                                                ((SoapFaultServiceException) cause).getServiceExceptionType().getMessageId(),
                                                cause, arg
            );
        }
    }

    public static String getMessage(String key, String... args) {
        if (args == null || args.length == 0) {
            return BUNDLE.getString(key);
        }
        return MessageFormat.format(BUNDLE.getString(key), (Object[]) args);
    }
}

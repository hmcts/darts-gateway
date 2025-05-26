package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class AuthenticationFailedException extends SoapFaultServiceException {


    public AuthenticationFailedException() {
        this(null, null);
    }

    public AuthenticationFailedException(String message) {
        this(message, null);
    }

    public AuthenticationFailedException(Throwable cause) {
        this(null, cause);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, cause,
              message == null ? "Please review the identities provided" : message);
    }
}

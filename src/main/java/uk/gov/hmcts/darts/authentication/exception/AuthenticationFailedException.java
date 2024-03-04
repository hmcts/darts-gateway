package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class AuthenticationFailedException extends SoapFaultServiceException {
    public AuthenticationFailedException(Throwable cause) {
        super(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, "dfs.authentication.exception", cause, new String[]{""});
    }

    public AuthenticationFailedException() {
        super(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED, "dfs.authentication.exception", null, new String[]{""});
    }
}

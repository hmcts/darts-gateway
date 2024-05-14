package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class RegisterNullServiceContextException extends SoapFaultServiceException {

    public RegisterNullServiceContextException() {
        super(FaultErrorCodes.E_NULL_CONTEXT_CHECK_LIBRARIES, null, "");
    }
}

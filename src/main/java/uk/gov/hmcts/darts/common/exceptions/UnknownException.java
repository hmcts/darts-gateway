package uk.gov.hmcts.darts.common.exceptions;

import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

public class UnknownException extends SoapFaultServiceException {
    public UnknownException() {
        super(FaultErrorCodes.E_UNSUPPORTED_EXCEPTION, new String[]{});
    }
}

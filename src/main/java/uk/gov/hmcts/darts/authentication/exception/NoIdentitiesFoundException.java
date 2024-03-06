package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class NoIdentitiesFoundException extends SoapFaultServiceException {
    public NoIdentitiesFoundException() {
        super(FaultErrorCodes.E_SERVICE_AUTHORIZATION_FAILED_INVALID_IDENTITIES, null, "");
    }
}

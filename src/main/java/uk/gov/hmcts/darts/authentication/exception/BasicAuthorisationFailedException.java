package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class BasicAuthorisationFailedException extends SoapFaultServiceException {

    public BasicAuthorisationFailedException(String serviceId) {
        super(FaultErrorCodes.E_BASIC_AUTHORISATION_FAILED, null, serviceId);
    }

}

package uk.gov.hmcts.darts.authentication.exception;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class DocumentumUnknownTokenSoapException extends SoapFaultServiceException {

    public DocumentumUnknownTokenSoapException(String token) {
        super(FaultErrorCodes.E_UNKNOWN_TOKEN, new String[]{token, "central"});
    }
}

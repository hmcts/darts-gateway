package uk.gov.hmcts.darts.common.exceptions.soap;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UnsupportedIdentitiesException extends SoapFaultServiceException {
    public UnsupportedIdentitiesException() {
        super(FaultErrorCodes.E_UNSUPPORTED_EXCEPTION, null, "");
    }
}

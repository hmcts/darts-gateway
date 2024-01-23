package uk.gov.hmcts.darts.common.exceptions.soap;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UnsupportedIdentitesException extends SoapFaultServiceException {
    public UnsupportedIdentitesException() {
        super(FaultErrorCodes.E_UNSUPPORTED_EXCEPTION, new String[]{});
    }
}

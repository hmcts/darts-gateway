package com.emc.documentum.fs.rt;

import jakarta.xml.bind.annotation.XmlRootElement;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;

@XmlRootElement
public class ServiceContextLookupException extends SoapFaultServiceException {

    public ServiceContextLookupException() {

    }

    public ServiceContextLookupException(String token) {
        super(FaultErrorCodes.E_UNKNOWN_TOKEN, null, token);
    }
}

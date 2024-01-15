package uk.gov.hmcts.darts.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import uk.gov.hmcts.darts.authentication.exception.ServiceException;

@Slf4j
public class DartsSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {
    @Override
    protected SoapFaultDefinition getFaultDefinition(Object endpoint, Exception ex) {

        if (ex instanceof ServiceException == false) {
        }

        log.error("An error occurred", ex);
        return super.getFaultDefinition(endpoint, ex);
    }
}

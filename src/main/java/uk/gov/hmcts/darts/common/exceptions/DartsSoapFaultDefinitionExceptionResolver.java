package uk.gov.hmcts.darts.common.exceptions;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapMessage;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;

import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

/**
 * This class handles all exception that leave the interface. It protects against data being leaked from the
 * api. This class will return data ONLY if it is a {@link uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException}
 */
@Slf4j
@Component
public class DartsSoapFaultDefinitionExceptionResolver extends AbstractEndpointExceptionResolver {

    private static final QName SERVICE_TYPE_QNAME = new QName("http://rt.fs.documentum.emc.com/", "ServiceException");

    @Override
    protected boolean resolveExceptionInternal(MessageContext messageContext, Object endpoint, Exception ex) {
        log.error("Error occurred", ex);
        SoapFaultServiceException serviceException = new UnknownException();
        if (ex instanceof SoapFaultServiceException) {
            serviceException = ((SoapFaultServiceException) ex);
        }
        final SoapMessage response = (SoapMessage) messageContext.getResponse();
        final SoapBody soapBody = response.getSoapBody();

        final SoapFault soapFault = soapBody.addServerOrReceiverFault(
            serviceException.getMessage(),
            Locale.ENGLISH
        );

        final SoapFaultDetail faultDetail = soapFault.addFaultDetail();
        final Result result = faultDetail.getResult();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ServiceExceptionType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            JAXBElement<ServiceExceptionType> serviceErrorDetail = new JAXBElement<>(
                SERVICE_TYPE_QNAME,
                ServiceExceptionType.class,
                null,
                serviceException.getServiceExceptionType()
            );
            jaxbMarshaller.marshal(serviceErrorDetail, result);
        } catch (JAXBException e) {
            log.error("Could not marshall soap detail type", e);
        }

        return true;
    }
}

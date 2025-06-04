package uk.gov.hmcts.darts.common.exceptions;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapMessage;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException;
import uk.gov.hmcts.darts.common.exceptions.soap.documentum.ServiceExceptionType;

import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

/**
 * This class handles all exception that leave the interface. It protects against data being leaked from the
 * api. This class will return data ONLY if it is a {@link uk.gov.hmcts.darts.common.exceptions.soap.SoapFaultServiceException}
 * or {@link uk.gov.hmcts.darts.common.exceptions.DartsException}
 */
@Slf4j
@Component
public class DartsSoapFaultDefinitionExceptionResolver extends AbstractEndpointExceptionResolver {

    public static final QName SERVICE_TYPE_QNAME = new QName("http://rt.fs.documentum.emc.com/", "ServiceException");

    @Override
    protected boolean resolveExceptionInternal(MessageContext messageContext, Object endpoint, Exception ex) {
        log.error("Error occurred", ex);
        if (ex instanceof DartsException dartsException) {
            return resolveDartsException(messageContext, dartsException);
        } else if (ex instanceof SoapFaultServiceException soapFaultServiceException) {
            return resolveSoapFaultServiceException(messageContext, soapFaultServiceException);
        } else {
            return resolveSoapFaultServiceException(messageContext, new UnknownException());
        }
    }

    private boolean resolveDartsException(MessageContext messageContext, DartsException dartsException) {
        String message = "Unexpected error occurred";
        if (dartsException.hasCodeAndMessage() && StringUtils.isNotBlank(dartsException.getCodeAndMessage().getMessage())) {
            message = dartsException.getCodeAndMessage().getMessage();
        } else {
            Throwable cause = dartsException.getCause();
            if (cause != null && StringUtils.isNotBlank(cause.getMessage())) {
                message = cause.getMessage();
            }
        }
        return resolveSoapFaultServiceException(messageContext, message,
                                                new ServiceExceptionType(FaultErrorCodes.E_UNKNOWN_CODE.name(),
                                                                         dartsException,
                                                                         dartsException.getMessage()));
    }

    private boolean resolveSoapFaultServiceException(MessageContext messageContext, SoapFaultServiceException serviceException) {
        return resolveSoapFaultServiceException(messageContext, serviceException.getMessage(), serviceException.getServiceExceptionType());
    }

    private boolean resolveSoapFaultServiceException(MessageContext messageContext, String message, ServiceExceptionType serviceExceptionType) {
        final SoapMessage response = (SoapMessage) messageContext.getResponse();
        final SoapBody soapBody = response.getSoapBody();

        final SoapFault soapFault = soapBody.addClientOrSenderFault(
            message,
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
                serviceExceptionType
            );
            jaxbMarshaller.marshal(serviceErrorDetail, result);
        } catch (JAXBException e) {
            log.error("Could not marshall soap detail type", e);
        }

        return true;
    }
}

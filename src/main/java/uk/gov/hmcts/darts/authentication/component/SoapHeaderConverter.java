package uk.gov.hmcts.darts.authentication.component;

import documentum.contextreg.ServiceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapHeaderElement;

import javax.xml.transform.Source;
import java.util.Optional;

@Component
public class SoapHeaderConverter {

    private JAXBContext jaxbContext;

    public SoapHeaderConverter() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(ServiceContext.class);
    }

    public Optional<ServiceContext> convertSoapHeader(SoapHeaderElement soapHeaderElement) {
        Optional<ServiceContext> serviceContextOptional = Optional.empty();
        final Source source = soapHeaderElement.getSource();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ServiceContext serviceContext = unmarshaller.unmarshal(source, ServiceContext.class).getValue();
            serviceContextOptional = Optional.ofNullable(serviceContext);
        } catch (JAXBException e) {
            // ignore
        }
        return serviceContextOptional;
    }

}

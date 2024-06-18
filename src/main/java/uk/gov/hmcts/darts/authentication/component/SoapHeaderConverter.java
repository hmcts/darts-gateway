package uk.gov.hmcts.darts.authentication.component;

import documentum.contextreg.ServiceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapHeaderElement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Optional;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

@Component
public class SoapHeaderConverter {

    private final JAXBContext jaxbContext;

    public SoapHeaderConverter() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(ServiceContext.class);
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
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

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public Optional<String> convertSoapHeaderToToken(SoapHeaderElement soapHeaderElement) {
        Optional<String> securityOptional = Optional.empty();

        // spring uses DOM Source as a default so lets use that and parse the model
        if (soapHeaderElement.getSource() instanceof DOMSource) {
            DOMSource source = (DOMSource) soapHeaderElement.getSource();

            if (source.getNode() instanceof Element) {
                Element el = (Element) source.getNode();
                NodeList list = el.getElementsByTagNameNS(
                    "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                    "BinarySecurityToken");

                if (list.getLength() == 1) {
                    securityOptional = Optional.of(list.item(0).getTextContent());
                }
            }
        }

        return securityOptional;
    }
}

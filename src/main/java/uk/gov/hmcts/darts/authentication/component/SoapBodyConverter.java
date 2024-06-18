package uk.gov.hmcts.darts.authentication.component;

import documentum.contextreg.Register;
import documentum.contextreg.ServiceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;
import uk.gov.hmcts.darts.utilities.NodeUtil;

import java.util.Optional;

@Component
@Slf4j
public class SoapBodyConverter {

    private final JAXBContext jaxbContext;

    public SoapBodyConverter() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(Register.class);
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    public Optional<ServiceContext> getServiceContext(SaajSoapMessage message) {
        Optional<ServiceContext> registerOptional = Optional.empty();
        try {
            SOAPBody soapBody = message.getSaajMessage().getSOAPPart().getEnvelope().getBody();
            Optional<Node> nodeOpt = NodeUtil.findNode("register.context", soapBody.getChildNodes());
            if (nodeOpt.isPresent()) {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                ServiceContext serviceContextObject = unmarshaller.unmarshal(nodeOpt.get(), ServiceContext.class).getValue();
                registerOptional = Optional.ofNullable(serviceContextObject);
            }
        } catch (SOAPException e) {
            log.error("Soap Exception while trying to retrieve register body", e);
        } catch (JAXBException e) {
            // ignore
        }
        return registerOptional;
    }

}

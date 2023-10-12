package uk.gov.hmcts.darts.utils.client;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocument;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntry;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLog;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import com.service.mojdarts.synapps.com.ObjectFactory;
import com.service.mojdarts.synapps.com.RegisterNode;
import com.service.mojdarts.synapps.com.RegisterNodeResponse;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.xml.transform.StringSource;

import java.lang.Exception;
import java.net.URL;
import java.util.function.Function;

/**
 * Simple client that demonstrates Mtom interaction for the darts api.
 */
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "unchecked"})
public class DartsGatewayMtomClient extends WebServiceGatewaySupport implements DartsGatewayClient {

    public DartsGatewayMtomClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    /**
     * sets the mode of this client to mtom supported or not.
     */
    @Override
    public void setEnableMtomMode(boolean enabled) {
        Marshaller marshaller = getWebServiceTemplate().getMarshaller();
        Unmarshaller unmarshaller = this.getWebServiceTemplate().getUnmarshaller();

        if (marshaller instanceof Jaxb2Marshaller) {
            ((Jaxb2Marshaller) marshaller).setMtomEnabled(enabled);
        }

        if (unmarshaller instanceof Jaxb2Marshaller) {
            ((Jaxb2Marshaller) unmarshaller).setMtomEnabled(enabled);
        }
    }

    @Override
    public <I, O> DartsGatewayAssertionUtil<O> sendMessage(URL uri, String payload,
                                                                               Function<I,
                                                                               JAXBElement<I>> supplier,
                                                                               Class<I> clazz, Function<Object,
        JAXBElement<O>> responseSupplier)
        throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<I> unmarshal = jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
        JAXBElement<I> ijaxbElement = supplier.apply(unmarshal.getValue());
        return new DartsGatewayAssertionUtil<>(responseSupplier.apply(getWebServiceTemplate().marshalSendAndReceive(
            uri.toString(),
            ijaxbElement
        )));
    }

    @Override
    public <C> JAXBElement<C> convertData(String payload, Class<C> clazz)
        throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
    }

    @Override
    public DartsGatewayAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           getCases -> new ObjectFactory().createGetCases(getCases),
                           GetCases.class,
                           getCases -> (JAXBElement<GetCasesResponse>) getCases
        );
    }

    @Override
    public DartsGatewayAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           addDocument -> new ObjectFactory().createAddDocument(addDocument),
                           AddDocument.class,
                           addDocument -> (JAXBElement<AddDocumentResponse>) addDocument
        );
    }

    @Override
    public void send(URL uri, String payload) throws Exception {
        getWebServiceTemplate().sendSourceAndReceiveToResult(
            uri.toString(),
            new StringSource(payload),
            new javax.xml.transform.Result() {
                @Override
                public void setSystemId(String systemId) {

                }

                @Override
                public String getSystemId() {
                    return null;
                }
            }
        );
    }

    @Override
    public DartsGatewayAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           getCaseLog -> new ObjectFactory().createGetCourtLog(getCaseLog),
                           GetCourtLog.class,
                           getCaseLog -> (JAXBElement<GetCourtLogResponse>) getCaseLog
        );
    }

    @Override
    public DartsGatewayAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           addLog -> new ObjectFactory().createAddLogEntry(addLog),
                           AddLogEntry.class,
                           addLog -> (JAXBElement<AddLogEntryResponse>) addLog
        );
    }

    @Override
    public DartsGatewayAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           addCases -> new ObjectFactory().createAddCase(addCases),
                           AddCase.class,
                           addCases -> (JAXBElement<AddCaseResponse>) addCases
        );
    }

    @Override
    public DartsGatewayAssertionUtil<RegisterNodeResponse> registerNode(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           registerNode -> new ObjectFactory().createRegisterNode(registerNode),
                           RegisterNode.class,
                           registerNode -> (JAXBElement<RegisterNodeResponse>) registerNode
        );
    }
}

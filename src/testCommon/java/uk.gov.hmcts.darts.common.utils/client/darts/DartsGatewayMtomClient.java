package uk.gov.hmcts.darts.common.utils.client.darts;

import com.emc.documentum.fs.rt.ServiceException;
import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.AddAudioResponse;
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
import jakarta.xml.bind.JAXBElement;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.common.utils.client.AbstractSoapTestClient;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;

import java.net.URL;

/**
 * Simple client that demonstrates Mtom interaction for the darts api.
 */
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "unchecked"})
public class DartsGatewayMtomClient extends AbstractSoapTestClient implements DartsGatewayClient {

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
    public SoapAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) {
        return sendMessage(uri, payload,
                           getCases -> new ObjectFactory().createGetCases(getCases),
                           GetCases.class,
                           getCases -> (JAXBElement<GetCasesResponse>) getCases
        );
    }

    @Override
    public SoapAssertionUtil<ServiceException> getCasesException(URL uri, String payload) {
        return sendMessage(uri, payload,
                           getCases -> new ObjectFactory().createGetCases(getCases),
                           GetCases.class,
                           getCases -> (JAXBElement<ServiceException>) getCases,
                           true
        );
    }

    @Override
    public SoapAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addDocument -> new ObjectFactory().createAddDocument(addDocument),
                           AddDocument.class,
                           addDocument -> (JAXBElement<AddDocumentResponse>) addDocument
        );
    }

    @Override
    public SoapAssertionUtil<ServiceException> addDocumentException(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addDocument -> new ObjectFactory().createAddDocument(addDocument),
                           AddDocument.class,
                           addDocument -> (JAXBElement<ServiceException>) addDocument,
                           true
        );
    }

    @Override
    public SoapAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) {
        return sendMessage(uri, payload,
                           getCaseLog -> new ObjectFactory().createGetCourtLog(getCaseLog),
                           GetCourtLog.class,
                           getCaseLog -> (JAXBElement<GetCourtLogResponse>) getCaseLog
        );
    }

    @Override
    public SoapAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addLog -> new ObjectFactory().createAddLogEntry(addLog),
                           AddLogEntry.class,
                           addLog -> (JAXBElement<AddLogEntryResponse>) addLog
        );
    }


    @Override
    public SoapAssertionUtil<ServiceException> postCourtLogsException(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addLog -> new ObjectFactory().createAddLogEntry(addLog),
                           AddLogEntry.class,
                           addLog -> (JAXBElement<ServiceException>) addLog,
                           true
        );
    }

    @Override
    public SoapAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addCases -> new ObjectFactory().createAddCase(addCases),
                           AddCase.class,
                           addCases -> (JAXBElement<AddCaseResponse>) addCases
        );
    }

    @Override
    public SoapAssertionUtil<RegisterNodeResponse> registerNode(URL uri, String payload) {
        return sendMessage(uri, payload,
                           registerNode -> new ObjectFactory().createRegisterNode(registerNode),
                           RegisterNode.class,
                           registerNode -> (JAXBElement<RegisterNodeResponse>) registerNode
        );
    }

    @Override
    public SoapAssertionUtil<AddAudioResponse> addAudio(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addAudio -> new ObjectFactory().createAddAudio(addAudio),
                           AddAudio.class,
                           addAudio -> (JAXBElement<AddAudioResponse>) addAudio
        );
    }

    @Override
    public SoapAssertionUtil<ServiceException> addAudioException(URL uri, String payload) {
        return sendMessage(uri, payload,
                           addAudio -> new ObjectFactory().createAddAudio(addAudio),
                           AddAudio.class,
                           addAudio -> (JAXBElement<ServiceException>) addAudio,
                           true
        );
    }


}

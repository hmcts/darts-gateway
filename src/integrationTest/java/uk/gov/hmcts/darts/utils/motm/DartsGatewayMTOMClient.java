package uk.gov.hmcts.darts.utils.motm;

import com.service.mojdarts.synapps.com.*;
import com.synapps.moj.dfs.response.CourtLog;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.xml.transform.StringSource;

import java.lang.Exception;
import java.net.URL;
import java.util.function.Function;

/**
 * Simple client that demonstrates MTOM by invoking
 */
@SuppressWarnings("unchecked")
public class DartsGatewayMTOMClient extends WebServiceGatewaySupport {

    public DartsGatewayMTOMClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    public <REQUEST, RESPONSE> DartsGatewayAssertionUtil<RESPONSE> sendMessage(URL uri, String payload, Function<REQUEST,JAXBElement<REQUEST>> supplier, Class<REQUEST> requestClass, Function<Object, JAXBElement<RESPONSE>>responseSupplier)
            throws Exception
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(requestClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<REQUEST> unmarshalResponse = jaxbUnmarshaller.unmarshal(new StringSource(payload), requestClass);
        JAXBElement<REQUEST> requestjaxbElement = supplier.apply(unmarshalResponse.getValue());
        return new DartsGatewayAssertionUtil<>(responseSupplier.apply(getWebServiceTemplate().marshalSendAndReceive(uri.toString(), requestjaxbElement)));
    }

    public <CONVERSION_DATA> JAXBElement<CONVERSION_DATA> convertData(String payload, Class<CONVERSION_DATA> requestClass) throws Exception
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(requestClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(new StringSource(payload), requestClass);
    }

    public DartsGatewayAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws Exception
    {
        return sendMessage(uri, payload,
                getCases -> new ObjectFactory().createGetCases(getCases),
                GetCases.class,
                getCases -> (JAXBElement<GetCasesResponse>)getCases
        );
    }

    public DartsGatewayAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) throws Exception
    {
        return sendMessage(uri, payload,
                addDocument -> new ObjectFactory().createAddDocument(addDocument),
                AddDocument.class,
                addDocument -> (JAXBElement<AddDocumentResponse>)addDocument
        );
    }

    public DartsGatewayAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) throws Exception
    {
        return sendMessage(uri, payload,
                getCaseLog -> new ObjectFactory().createGetCourtLog(getCaseLog),
                GetCourtLog.class,
                getCaseLog -> (JAXBElement<GetCourtLogResponse>)getCaseLog
        );
    }

    public DartsGatewayAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) throws Exception
    {
        return sendMessage(uri, payload,
                addLog -> new ObjectFactory().createAddLogEntry(addLog),
                AddLogEntry.class,
                addLog -> (JAXBElement<AddLogEntryResponse>)addLog
        );
    }

    public DartsGatewayAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws Exception
    {
        return sendMessage(uri, payload,
                addCases -> new ObjectFactory().createAddCase(addCases),
                AddCase.class,
                addCases -> (JAXBElement<AddCaseResponse>)addCases
        );
    }
}

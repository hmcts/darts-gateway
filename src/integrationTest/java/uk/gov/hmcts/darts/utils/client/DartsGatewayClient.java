package uk.gov.hmcts.darts.utils.client;

import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import jakarta.xml.bind.JAXBElement;

import java.net.URL;
import java.util.function.Function;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface DartsGatewayClient {
    /**
     * sets the mode of this client.
     */
    void setEnableMtomMode(boolean enabled);

    <I, O> DartsGatewayAssertionUtil<O> sendMessage(URL uri, String payload, Function<I, JAXBElement<I>> supplier,
                                                    Class<I> clazz, Function<Object, JAXBElement<O>> responseSupplier)
            throws Exception;

    <C> JAXBElement<C> convertData(String payload, Class<C> clazz) throws Exception;

    DartsGatewayAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) throws Exception;

    void send(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws Exception;
}

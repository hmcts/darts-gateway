package uk.gov.hmcts.darts.utils.client;

import com.service.mojdarts.synapps.com.*;
import jakarta.xml.bind.JAXBElement;

import java.lang.Exception;
import java.net.URL;
import java.util.function.Function;

public interface DartsGatewayClientable {
    /**
     * sets the mode of this client
     */
    void setEnableMTOMMode(boolean enabled);

    <REQUEST, RESPONSE> DartsGatewayAssertionUtil<RESPONSE> sendMessage(URL uri, String payload, Function<REQUEST, JAXBElement<REQUEST>> supplier, Class<REQUEST> requestClass, Function<Object, JAXBElement<RESPONSE>> responseSupplier)
            throws Exception;

    <CONVERSION_DATA> JAXBElement<CONVERSION_DATA> convertData(String payload, Class<CONVERSION_DATA> requestClass) throws Exception;

    DartsGatewayAssertionUtil<GetCasesResponse> getCases(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<AddDocumentResponse> addDocument(URL uri, String payload) throws Exception;

    void send(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<GetCourtLogResponse> getCourtLogs(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<AddLogEntryResponse> postCourtLogs(URL uri, String payload) throws Exception;

    DartsGatewayAssertionUtil<AddCaseResponse> addCases(URL uri, String payload) throws Exception;
}

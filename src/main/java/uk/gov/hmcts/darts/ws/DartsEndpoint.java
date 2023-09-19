package uk.gov.hmcts.darts.ws;

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
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.cases.CasesRoute;
import uk.gov.hmcts.darts.courtlogs.AddCourtLogsRoute;
import uk.gov.hmcts.darts.courtlogs.GetCourtLogRoute;
import uk.gov.hmcts.darts.routing.EventRoutingService;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class DartsEndpoint {
    private final EventRoutingService eventRoutingService;
    private final CasesRoute casesRoute;
    private final GetCourtLogRoute getCourtLogRoute;
    private final AddCourtLogsRoute addCourtLogsRoute;
    private final DartsEndpointHandler endpointHandler;

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addDocument")
    @ResponsePayload
    public JAXBElement<AddDocumentResponse> addDocument(@RequestPayload JAXBElement<AddDocument> addDocument) {
        AddDocumentResponse documentResponse = ResponseFactory.getAddDocumentResponse();

        documentResponse.setReturn(endpointHandler.makeAPICall("addDocument", () -> eventRoutingService.route(addDocument.getValue()),
                                           documentResponse::getReturn));

        return new ObjectFactory().createAddDocumentResponse(documentResponse);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCases")
    @ResponsePayload
    public JAXBElement<GetCasesResponse> getCases(@RequestPayload JAXBElement<GetCases> getCases) {
        GetCasesResponse casesResponse =  ResponseFactory.getCasesResponse();

        casesResponse.setReturn(endpointHandler.makeAPICall("getCases", () -> casesRoute.route(getCases.getValue()),
                                                            casesResponse::getReturn));

        return new ObjectFactory().createGetCasesResponse(casesResponse);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addCase")
    @ResponsePayload
    public JAXBElement<AddCaseResponse> addCase(@RequestPayload JAXBElement<AddCase> addCase) {
        AddCaseResponse addCaseResponse = ResponseFactory.getAddCaseResponse();

        addCaseResponse.setReturn(endpointHandler.makeAPICall("getCases", () -> casesRoute.route(addCase.getValue()),
                                                            addCaseResponse::getReturn));

        return new ObjectFactory().createAddCaseResponse(addCaseResponse);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCourtLog")
    @ResponsePayload
    public JAXBElement<GetCourtLogResponse> getCourtLogResponse(@RequestPayload JAXBElement<GetCourtLog> getCourtLog) {

        GetCourtLogResponse addCaseResponseLog = ResponseFactory.getCourtLogResponse();

        addCaseResponseLog.setReturn(endpointHandler.makeAPICall("getCases", () -> getCourtLogRoute.route(getCourtLog.getValue()),
                                    addCaseResponseLog::getReturn));

        return new ObjectFactory().createGetCourtLogResponse(addCaseResponseLog);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addLogEntry")
    @ResponsePayload
    public JAXBElement<AddLogEntryResponse> addLogEntry(@RequestPayload JAXBElement<AddLogEntry> addLogEntry) {

        AddLogEntryResponse addLogEntryResponse = ResponseFactory.getAddLogEntryResponse();

        addLogEntryResponse.setReturn(endpointHandler.makeAPICall("getCases", () -> addCourtLogsRoute.route(addLogEntry.getValue().getDocument()),
                                   addLogEntryResponse::getReturn));

        return new ObjectFactory().createAddLogEntryResponse(addLogEntryResponse);
    }
}

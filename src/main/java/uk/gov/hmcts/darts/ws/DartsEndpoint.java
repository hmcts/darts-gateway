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
import feign.FeignException;
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
    private final DartsResponseUtils utils;

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addDocument")
    @ResponsePayload
    public JAXBElement<AddDocumentResponse> addDocument(@RequestPayload JAXBElement<AddDocument> addDocument) {
        ObjectFactory factory = new ObjectFactory();
        var addDocumentResponse = new AddDocumentResponse();
        try {
            addDocumentResponse = eventRoutingService.route(addDocument.getValue());
        } catch (FeignException e) {
            log.error("Error sending addDocument. {}", e.contentUTF8(), e);
            addDocumentResponse.setReturn(utils.createDartsResponseMessage(e));
        } catch (DartsException de) {
            log.error("Error sending addDocument. {}", de);
            addDocumentResponse.setReturn(utils.createDartsResponseMessage(de.getCodeAndMessage()));
        }

        return factory.createAddDocumentResponse(addDocumentResponse);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCases")
    @ResponsePayload
    public JAXBElement<GetCasesResponse> getCases(@RequestPayload JAXBElement<GetCases> getCases) {

        ObjectFactory factory = new ObjectFactory();

        return factory.createGetCasesResponse(casesRoute.route(getCases.getValue()));
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addCase")
    @ResponsePayload
    public JAXBElement<AddCaseResponse> addCase(@RequestPayload JAXBElement<AddCase> addCase) {
        ObjectFactory factory = new ObjectFactory();

        try {
            casesRoute.route(addCase.getValue());

            return factory.createAddCaseResponse(utils.createSuccessfulAddCaseResponse());
        } catch (DartsException e) {
            return factory.createAddCaseResponse(utils.createErrorAddCaseResponse(e));
        }
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCourtLog")
    @ResponsePayload
    public JAXBElement<GetCourtLogResponse> getCourtLogResponse(@RequestPayload JAXBElement<GetCourtLog> getCourtLog) {
        var getCourtLogResponse = new GetCourtLogResponse();
        ObjectFactory factory = new ObjectFactory();

        try {
            getCourtLogResponse = getCourtLogRoute.route(getCourtLog.getValue());

        } catch (DartsException e) {
            getCourtLogResponse.setReturn(utils.createCourtLogResponse(e));
            return factory.createGetCourtLogResponse(getCourtLogResponse);
        }

        return factory.createGetCourtLogResponse(getCourtLogResponse);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addLogEntry")
    @ResponsePayload
    public JAXBElement<AddLogEntryResponse> addLogEntry(@RequestPayload JAXBElement<AddLogEntry> addLogEntry) {
        var addLogEntryResponse = new AddLogEntryResponse();
        ObjectFactory factory = new ObjectFactory();

        try {
            addLogEntryResponse = addCourtLogsRoute.route(addLogEntry.getValue().getDocument());

        } catch (DartsException e) {
            log.error(e.getMessage());
            addLogEntryResponse.setReturn(utils.createCourtLogResponse(e));
            return factory.createAddLogEntryResponse(addLogEntryResponse);
        }

        return factory.createAddLogEntryResponse(addLogEntryResponse);
    }


}

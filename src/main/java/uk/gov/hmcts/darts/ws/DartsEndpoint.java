package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.*;
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

import java.lang.Exception;

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
    public AddDocumentResponse addDocument(@RequestPayload JAXBElement<AddDocument> addDocument) {
        var addDocumentResponse = new AddDocumentResponse();
        try {
            addDocumentResponse = eventRoutingService.route(addDocument.getValue());
        } catch (Exception e) {
            addDocumentResponse.setReturn(utils.createDartsResponseMessage(e));
            return addDocumentResponse;
        }

        return addDocumentResponse;
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCases")
    @ResponsePayload
    public GetCasesResponse getCases(@RequestPayload JAXBElement<GetCases> getCases) {
        return casesRoute.route(getCases.getValue());
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addCase")
    @ResponsePayload
    public AddCaseResponse addCase( @RequestPayload AddCase addCase) {

        System.out.println("");
        /*
        try {
            casesRoute.route(addCase);
            return utils.createSuccessfulAddCaseResponse();
        } catch (Exception e) {
            return utils.createErrorAddCaseResponse(e);
        }

         */

        return null;
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCourtLog")
    @ResponsePayload
    public GetCourtLogResponse getCourtLogResponse(@RequestPayload JAXBElement<GetCourtLog> getCourtLog) {
        var getCourtLogResponse = new GetCourtLogResponse();
        try {
            getCourtLogResponse = getCourtLogRoute.route(getCourtLog.getValue());

        } catch (Exception e) {
            getCourtLogResponse.setReturn(utils.createCourtLogResponse(e));
            return getCourtLogResponse;
        }

        return getCourtLogResponse;
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addLogEntry")
    @ResponsePayload
    public AddLogEntryResponse addLogEntry(@RequestPayload JAXBElement<AddLogEntry> addLogEntry) {
        var addLogEntryResponse = new AddLogEntryResponse();

        try {
            addLogEntryResponse = addCourtLogsRoute.route(addLogEntry.getValue().getDocument());

        } catch (Exception e) {
            log.error(e.getMessage());
            addLogEntryResponse.setReturn(utils.createCourtLogResponse(e));
            return addLogEntryResponse;
        }

        return addLogEntryResponse;
    }


}

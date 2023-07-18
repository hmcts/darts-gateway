package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddDocument;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.GetCases;
import com.service.mojdarts.synapps.com.GetCasesResponse;
import com.service.mojdarts.synapps.com.GetCourtLog;
import com.service.mojdarts.synapps.com.GetCourtLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.cases.CasesRoute;
import uk.gov.hmcts.darts.courtlogs.GetCourtLogRoute;
import uk.gov.hmcts.darts.routing.EventRoutingService;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class DartsEndpoint {

    private final EventRoutingService eventRoutingService;
    private final CasesRoute casesRoute;

    private final GetCourtLogRoute getCourtLogRoute;
    private final DartsResponseUtils utils;

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addDocument")
    @ResponsePayload
    public AddDocumentResponse addDocument(@RequestPayload AddDocument addDocument) {
        var addDocumentResponse = new AddDocumentResponse();
        try {
            addDocumentResponse = eventRoutingService.route(addDocument);
        } catch (Exception e) {
            addDocumentResponse.setReturn(utils.createDartsResponseMessage(e));
            return addDocumentResponse;
        }

        return addDocumentResponse;
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCases")
    @ResponsePayload
    public GetCasesResponse getCases(@RequestPayload GetCases getCases) {
        return casesRoute.getCases(getCases);
    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "getCourtLog")
    @ResponsePayload
    public GetCourtLogResponse getCourtLogResponse(@RequestPayload GetCourtLog getCourtLog) {
        var getCourtLogResponse = new GetCourtLogResponse();
        try {
            getCourtLogResponse = getCourtLogRoute.route(getCourtLog);

        } catch (Exception e) {
            getCourtLogResponse.setReturn(utils.createCourtLogResponse(e));
            return getCourtLogResponse;
        }

        return getCourtLogResponse;
    }


}

package uk.gov.hmcts.darts.soap;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.AddCaseResponse;
import com.service.mojdarts.synapps.com.AddDocument;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.routing.DartsRoutingService;

import static uk.gov.hmcts.darts.soap.ErrorCodeAndMessage.OK;

@Endpoint
@RequiredArgsConstructor
public class DartsEndpoint {

    private final DartsRoutingService dartsRoutingService;

    private final DartsResponseUtils utils;

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addDocument")
    @ResponsePayload
    public AddDocumentResponse addDocument(@RequestPayload AddDocument addDocument) {
        var addDocumentResponse = new AddDocumentResponse();
        try {
            dartsRoutingService.route(addDocument);
        } catch (Exception e) {
            addDocumentResponse.setReturn(utils.createResponseMessage(e));
            return addDocumentResponse;
        }
        addDocumentResponse.setReturn(utils.createResponseMessage(OK));

        return addDocumentResponse;

    }

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addCase")
    @ResponsePayload
    public AddCaseResponse addCase(@RequestPayload AddCase addCase) {
        var addCaseResponse = new AddCaseResponse();
        try {
            dartsRoutingService.route(addCase);
        } catch (Exception e) {
            addCaseResponse.setReturn(utils.createResponseMessage(e));
            return addCaseResponse;
        }
        addCaseResponse.setReturn(utils.createResponseMessage(OK));

        return addCaseResponse;
    }


}

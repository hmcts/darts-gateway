package uk.gov.hmcts.darts.ws;

import com.service.mojdarts.synapps.com.AddDocument;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.routing.DartsRoutingService;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class DartsEndpoint {

    private final DartsRoutingService dartsRoutingService;

    private final DartsResponseUtils utils;

    @PayloadRoot(namespace = "http://com.synapps.mojdarts.service.com", localPart = "addDocument")
    @ResponsePayload
    public AddDocumentResponse addDocument(@RequestPayload AddDocument addDocument) {
        var addDocumentResponse = new AddDocumentResponse();
        try {
            addDocumentResponse = dartsRoutingService.route(addDocument);
        } catch (Exception e) {
            addDocumentResponse.setReturn(utils.createResponseMessage(e));
            return addDocumentResponse;
        }

        return addDocumentResponse;
    }

}

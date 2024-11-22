package uk.gov.hmcts.darts.event.service.impl;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.common.client.EventsClient;
import uk.gov.hmcts.darts.model.event.EventsResponse;
import uk.gov.hmcts.darts.utilities.MapperUtility;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;

@Service
@RequiredArgsConstructor
public class EventRoute {

    @Value("${darts-gateway.events.schema}")
    private String schemaPath;
    @Value("${darts-gateway.events.validate}")
    private boolean validateEvent;
    private final XmlValidator xmlValidator;
    private final EventsClient eventsClient;
    private final EventRequestMapper dartsXmlMapper;

    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public DARTSResponse handle(String document, String messageId, String type, String subType) {
        if (validateEvent) {
            xmlValidator.validate(document, schemaPath);
        }

        DartsEvent dartsEvent = XmlParser.unmarshal(document, DartsEvent.class);
        uk.gov.hmcts.darts.model.event.DartsEvent eventRequest = dartsXmlMapper.toNewApi(dartsEvent, messageId, type, subType);

        ResponseEntity<EventsResponse> eventResponse = eventsClient.eventsPost(eventRequest);

        return MapperUtility.mapResponse(eventResponse.getBody(), true);
    }
}

package uk.gov.hmcts.darts.event.service.impl;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Processing event with messageId: {}, type: {}, subType: {}", messageId, type, subType);
        if (validateEvent) {
            xmlValidator.validate(document, schemaPath);
        }

        DartsEvent dartsEvent = XmlParser.unmarshal(document, DartsEvent.class);
        uk.gov.hmcts.darts.model.event.DartsEvent eventRequest = dartsXmlMapper.toNewApi(dartsEvent, messageId, type, subType);
        log.info("Start DartsEvent calling eventsClient with eventRequest");
        ResponseEntity<EventsResponse> eventResponse = eventsClient.eventsPost(eventRequest);
        log.info("End DartsEvent calling eventsClient with eventResponse");

        DARTSResponse response = MapperUtility.mapResponse(eventResponse.getBody(), true);
        log.info("DartsResponse returned");
        return response;
    }
}

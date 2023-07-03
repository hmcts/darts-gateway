package uk.gov.hmcts.darts.event.service.impl;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.event.model.EventResponse;
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
    private final DartsFeignClient dartsFeignClient;
    private final XmlParser xmlParser;
    private final LegacyDartsToNewApiMapper dartsXmlMapper;

    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public AddDocumentResponse handle(String document, String messageId, String type, String subType) {
        if (validateEvent) {
            xmlValidator.validate(document, schemaPath);
        }

        var dartsEvent = xmlParser.unmarshal(document, DartsEvent.class);
        var eventRequest = dartsXmlMapper.toNewApi(dartsEvent, messageId, type, subType);

        EventResponse eventResponse = dartsFeignClient.sendEvent(eventRequest);

        return dartsXmlMapper.toLegacyApi(eventResponse);
    }
}

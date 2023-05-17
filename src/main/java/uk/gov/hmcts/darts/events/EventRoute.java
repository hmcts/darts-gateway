package uk.gov.hmcts.darts.events;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.hmcts.darts.utilities.XmlParser;

@Service
@AllArgsConstructor
public class EventRoute {
    private final EventApiClient eventApiClient;
    private final XmlParser xmlParser;
    private final EventXmlToJsonMapper dartsXmlMapper;

    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    public void send(String document, String messageId, String type, String subType) {
        DartsEvent dartsEvent = xmlParser.unmarshal(document, DartsEvent.class);
        eventApiClient.sendEvent(
            dartsXmlMapper.toJson(dartsEvent, type, subType));
    }
}

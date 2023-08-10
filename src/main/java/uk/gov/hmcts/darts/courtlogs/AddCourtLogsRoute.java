package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.AddDocumentResponse;
import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.courtservice.events.LogEntry;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.event.model.EventResponse;
import uk.gov.hmcts.darts.model.events.CourtLogsPostRequestBody;
import uk.gov.hmcts.darts.utilities.MapperUtility;
import uk.gov.hmcts.darts.utilities.XmlParser;

@RequiredArgsConstructor
@Service
public class AddCourtLogsRoute {

    private final XmlParser xmlParser;
    private final AddCourtLogsMapper mapper;
    private final DartsFeignClient dartsFeignClient;

    public AddLogEntryResponse route(String document) {

        LogEntry logEntry = xmlParser.unmarshal(document, LogEntry.class);
        CourtLogsPostRequestBody postRequestBody = mapper.mapToApi(logEntry);

        EventResponse response = dartsFeignClient.postCourtLogs(postRequestBody);

        return MapperUtility.mapResponse(response);
    }

}

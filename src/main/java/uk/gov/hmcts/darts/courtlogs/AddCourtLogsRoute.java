package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.AddLogEntryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.event.model.EventResponse;
import uk.gov.hmcts.darts.model.event.CourtLogsPostRequestBody;
import uk.gov.hmcts.darts.utilities.MapperUtility;
import uk.gov.hmcts.darts.utilities.XmlParser;

@RequiredArgsConstructor
@Service
@Slf4j
public class AddCourtLogsRoute {

    private final XmlParser xmlParser;
    private final AddCourtLogsMapper mapper;
    private final DartsFeignClient dartsFeignClient;

    public AddLogEntryResponse route(String document) {

        EventResponse response;

        LogEntry logEntry = xmlParser.unmarshal(document, LogEntry.class);
        CourtLogsPostRequestBody postRequestBody = mapper.mapToApi(logEntry);

        try {
            response = dartsFeignClient.postCourtLogs(postRequestBody);
        } catch (HttpClientErrorException ce) {
            log.error("Failure calling Darts API", ce);
            throw ce;
        }

        return MapperUtility.mapResponse(response);
    }

}

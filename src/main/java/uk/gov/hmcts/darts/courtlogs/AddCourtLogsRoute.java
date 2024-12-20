package uk.gov.hmcts.darts.courtlogs;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.darts.addlogentry.LogEntry;
import uk.gov.hmcts.darts.common.client.CourtLogsClient;
import uk.gov.hmcts.darts.model.event.CourtLogsPostRequestBody;
import uk.gov.hmcts.darts.model.event.EventsResponse;
import uk.gov.hmcts.darts.utilities.MapperUtility;
import uk.gov.hmcts.darts.utilities.XmlParser;

@RequiredArgsConstructor
@Service
@Slf4j
public class AddCourtLogsRoute {
    private final AddCourtLogsMapper mapper;
    private final CourtLogsClient courtLogsClient;

    public DARTSResponse route(String document) {

        ResponseEntity<EventsResponse> response;

        LogEntry logEntry = XmlParser.unmarshal(document, LogEntry.class);
        CourtLogsPostRequestBody postRequestBody = mapper.mapToApi(logEntry);

        try {
            response = courtLogsClient.courtlogsPost(postRequestBody);
        } catch (HttpClientErrorException ce) {
            log.error("Failure calling Darts API", ce);
            throw ce;
        }

        return MapperUtility.mapResponse(response.getBody(), true);
    }

}

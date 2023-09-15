package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.darts.common.client.CourtLogsClient;
import uk.gov.hmcts.darts.common.util.DateConverters;
import uk.gov.hmcts.darts.model.event.CourtLog;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCourtLogRouteTest {

    private final OffsetDateTime offsetStartTime = OffsetDateTime.now().minusDays(1);
    private final OffsetDateTime offsetEndTime = OffsetDateTime.now();

    @Mock
    private CourtLogsClient courtLogsClient;
    @Mock
    private GetCourtLogsMapper getCourtLogsMapper;
    @Mock
    private DateConverters dateConverters;

    private GetCourtLogRoute getCourtLogRoute;

    @BeforeEach
    void setUp() {
        getCourtLogRoute = new GetCourtLogRoute(courtLogsClient, getCourtLogsMapper, dateConverters);

        GetCourtLog legacyCourtLogRequest = someLegacyGetCourtLogRequest();

        when(dateConverters.offsetDateTimeFrom(legacyCourtLogRequest.getStartTime()))
              .thenReturn(offsetStartTime);
        when(dateConverters.offsetDateTimeFrom(legacyCourtLogRequest.getEndTime()))
              .thenReturn(offsetEndTime);
    }

    @Test
    void callsGetLogApiClientWithCorrectParameters() {
        var legacyGetCourtLog = someLegacyGetCourtLogRequest();

        ArrayList<CourtLog> courtLogs = new ArrayList<>();
        ResponseEntity<List<CourtLog>> entity = new ResponseEntity<>(courtLogs, HttpStatusCode.valueOf(200));
        when(courtLogsClient.courtlogsGet(notNull(), notNull(), notNull(), notNull())).thenReturn(entity);

        getCourtLogRoute.route(legacyGetCourtLog);

        verify(courtLogsClient).courtlogsGet(
              "some-court-house",
              "some-court-house",
              offsetStartTime,
              offsetEndTime);
    }

    private GetCourtLog someLegacyGetCourtLogRequest() {
        var getCourtLog = new GetCourtLog();
        getCourtLog.setCourthouse("some-court-house");
        getCourtLog.setCaseNumber("some-court-house");
        getCourtLog.setStartTime("20221228235959");
        getCourtLog.setEndTime("20231228235959");

        return getCourtLog;
    }
}

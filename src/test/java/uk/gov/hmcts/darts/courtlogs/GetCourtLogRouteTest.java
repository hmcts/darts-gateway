package uk.gov.hmcts.darts.courtlogs;

import com.service.mojdarts.synapps.com.GetCourtLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.common.client.DartsFeignClient;
import uk.gov.hmcts.darts.common.util.DateConverters;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCourtLogRouteTest {

    private final OffsetDateTime offsetStartTime = OffsetDateTime.now().minusDays(1);
    private final OffsetDateTime offsetEndTime = OffsetDateTime.now();

    @Mock
    DartsFeignClient dartsFeignClient;
    @Mock
    GetCourtLogsMapper getCourtLogsMapper;
    @Mock
    DateConverters dateConverters;

    GetCourtLog legacyCourtLogRequest;
    GetCourtLogRoute getCourtLogRoute;

    @BeforeEach
    void setUp() {
        getCourtLogRoute = new GetCourtLogRoute(dartsFeignClient, getCourtLogsMapper, dateConverters);

        legacyCourtLogRequest = someLegacyGetCourtLogRequest();

        when(dateConverters.offsetDateTimeFrom(legacyCourtLogRequest.getStartTime()))
              .thenReturn(offsetStartTime);
        when(dateConverters.offsetDateTimeFrom(legacyCourtLogRequest.getEndTime()))
              .thenReturn(offsetEndTime);
    }

    @Test
    void callsGetLogApiClientWithCorrectParameters() {
        var legacyGetCourtLog = someLegacyGetCourtLogRequest();

        getCourtLogRoute.route(legacyGetCourtLog);

        verify(dartsFeignClient).getCourtLogs(
              "some-court-house",
              "some-court-house",
              offsetStartTime,
              offsetEndTime
        );
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
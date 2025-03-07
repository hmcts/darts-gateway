package uk.gov.hmcts.darts.routing;

import com.service.mojdarts.synapps.com.AddDocument;
import com.synapps.moj.dfs.response.DARTSResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.cases.CaseInfoRoute;
import uk.gov.hmcts.darts.dailylist.DailyListRoute;
import uk.gov.hmcts.darts.event.service.impl.EventRoute;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRoutingServiceTest {

    @Mock
    private DailyListRoute dailyListRoute;
    @Mock
    private EventRoute eventRoute;
    @Mock
    private CaseInfoRoute caseInfoRoute;

    @InjectMocks
    private EventRoutingService eventRoutingService;

    @ParameterizedTest
    @ValueSource(strings = {"NEWCASE", "UPDCASE"})
    void route_shouldCallCaseInfo_whenACaseTypeIsUse(String caseType) {
        DARTSResponse dartsResponse = mock(DARTSResponse.class);
        when(caseInfoRoute.handle(any(), any())).thenReturn(dartsResponse);

        AddDocument addDocument = mock(AddDocument.class);
        when(addDocument.getType()).thenReturn(caseType);
        String document = "some-document";
        when(addDocument.getDocument()).thenReturn(document);
        assertThat(eventRoutingService.route(addDocument)).isEqualTo(dartsResponse);

        verify(caseInfoRoute).handle(document, caseType);
        verifyNoInteractions(eventRoute, dailyListRoute);
    }
}

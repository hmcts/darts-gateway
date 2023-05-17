package uk.gov.hmcts.darts.routing;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.AddDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.darts.cases.AddCaseRoute;
import uk.gov.hmcts.darts.dailylist.DailyListRoute;
import uk.gov.hmcts.darts.events.EventRoute;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
class DartsRoutingServiceTest {

    @Mock
    DailyListRoute dailyListRoute;
    @Mock
    EventRoute eventRoute;
    @Mock
    AddCaseRoute addCaseRoute;
    @Mock
    AddCase addCase;
    @Mock
    AddDocument addDocument;

    @InjectMocks
    DartsRoutingService dartsRoutingService;

    @Test
    void routesAddCaseRequests() {
        when(addCase.getDocument()).thenReturn("some-document");

        dartsRoutingService.route(addCase);

        verify(addCaseRoute).route("some-document");

    }

    @Test
    void routesEventRequests() {
        when(addDocument.getMessageId()).thenReturn("some-id");
        when(addDocument.getType()).thenReturn("some-subtype");
        when(addDocument.getSubType()).thenReturn("some-subtype");
        when(addDocument.getDocument()).thenReturn("some-document");

        dartsRoutingService.route(addDocument);

        verify(eventRoute).send("some-document", "some-id", "some-subtype", "some-subtype");
    }

    @Test
    void routesDailyListsRequests() {
        when(addDocument.getMessageId()).thenReturn("some-id");
        when(addDocument.getType()).thenReturn("DL");
        when(addDocument.getDocument()).thenReturn("some-document");

        dartsRoutingService.route(addDocument);

        verify(dailyListRoute).route("some-document", "some-id");
    }

    @Test
    void routesEventsRequests() {
        when(addDocument.getMessageId()).thenReturn("some-id");
        when(addDocument.getType()).thenReturn("some-subtype");
        when(addDocument.getSubType()).thenReturn("some-subtype");
        when(addDocument.getDocument()).thenReturn("some-document");

        dartsRoutingService.route(addDocument);

        verify(eventRoute).send("some-document", "some-id", "some-subtype", "some-subtype");
    }
}

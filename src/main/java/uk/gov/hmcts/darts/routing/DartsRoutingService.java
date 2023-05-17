package uk.gov.hmcts.darts.routing;

import com.service.mojdarts.synapps.com.AddCase;
import com.service.mojdarts.synapps.com.AddDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.AddCaseRoute;
import uk.gov.hmcts.darts.dailylist.DailyListRoute;
import uk.gov.hmcts.darts.events.EventRoute;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Service
@RequiredArgsConstructor
public class DartsRoutingService {

    private static final Set<String> DAILY_LIST_TYPES = new HashSet<>(asList("CPPDL", "DL"));

    private final DailyListRoute dailyListRoute;
    private final EventRoute eventRoute;
    private final AddCaseRoute addCaseRoute;

    public void route(AddDocument request) {
        if (DAILY_LIST_TYPES.contains(request.getType())) {
            dailyListRoute.route(request.getDocument(), request.getMessageId());
        } else {
            eventRoute.send(request.getDocument(), request.getMessageId(), request.getType(), request.getSubType());
        }
    }

    public void route(AddCase addCase) {
        addCaseRoute.route(addCase.getDocument());
    }
}

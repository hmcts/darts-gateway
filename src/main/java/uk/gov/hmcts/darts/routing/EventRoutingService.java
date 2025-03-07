package uk.gov.hmcts.darts.routing;

import com.service.mojdarts.synapps.com.AddDocument;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.cases.CaseInfoRoute;
import uk.gov.hmcts.darts.dailylist.DailyListRoute;
import uk.gov.hmcts.darts.event.service.impl.EventRoute;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Service
@RequiredArgsConstructor
public class EventRoutingService {

    public static final Set<String> DAILY_LIST_TYPES = new HashSet<>(asList("CPPDL", "DL"));
    public static final Set<String> CASE_INFO_TYPES = new HashSet<>(asList("NEWCASE", "UPDCASE"));

    private final DailyListRoute dailyListRoute;
    private final EventRoute eventRoute;
    private final CaseInfoRoute caseInfoRoute;

    public DARTSResponse route(AddDocument request) {
        final String type = request.getType();
        if (DAILY_LIST_TYPES.contains(type)) {
            return dailyListRoute.handle(request, type);
        } else if (CASE_INFO_TYPES.contains(type)) {
            return caseInfoRoute.handle(request.getDocument(), type);
        } else {
            return eventRoute.handle(
                request.getDocument(),
                request.getMessageId(),
                type,
                request.getSubType()
            );
        }
    }
}

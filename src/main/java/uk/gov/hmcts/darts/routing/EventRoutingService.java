package uk.gov.hmcts.darts.routing;

import com.service.mojdarts.synapps.com.AddDocument;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.dailylist.DailyListRoute;
import uk.gov.hmcts.darts.event.service.impl.EventRoute;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Service
@RequiredArgsConstructor
public class EventRoutingService {

    public static final Set<String> DAILY_LIST_TYPES = new HashSet<>(asList("CPPDL", "DL"));

    private final DailyListRoute dailyListRoute;
    private final EventRoute eventRoute;

    public DARTSResponse route(AddDocument request) {
        if (DAILY_LIST_TYPES.contains(request.getType())) {
            return dailyListRoute.handle(request, request.getType());
        } else {
            return eventRoute.handle(
                request.getDocument(),
                request.getMessageId(),
                request.getType(),
                request.getSubType()
            );
        }
    }
}

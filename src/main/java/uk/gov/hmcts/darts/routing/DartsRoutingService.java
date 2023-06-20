package uk.gov.hmcts.darts.routing;

import com.service.mojdarts.synapps.com.AddDocument;
import com.service.mojdarts.synapps.com.AddDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public AddDocumentResponse route(AddDocument request) {
        if (DAILY_LIST_TYPES.contains(request.getType())) {
            return dailyListRoute.handle(request.getDocument(), request.getMessageId());
        } else {
            return eventRoute.handle(
                request.getDocument(),
                request.getMessageId(),
                request.getType(),
                request.getSubType());
        }
    }


}

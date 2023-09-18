package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class DartsEndpointHandler {

    public <T extends DARTSResponse> T makeAPICall(String taskName, Supplier<T> execute, Supplier<T> createResponse) {
        try {
            return execute.get();
        }  catch (DartsException de) {
            log.error("Error sending " + taskName + "{}", de);
            T response = createResponse.get();
            response.setCode(de.getCodeAndMessage().getCode());
            response.setMessage(de.getCodeAndMessage().getMessage());

            return response;
        }
    }
}

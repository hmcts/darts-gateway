package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.exceptions.DartsException;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class DartsEndpointHandler {

    public <T extends DARTSResponse> T makeAPICall(String taskName, Supplier<T> execute, Supplier<T> createResponse) {
        try {
            return execute.get();
        } catch (DartsException de) {
            if (de.hasCodeAndMessage() && de.getCodeAndMessage().isSuccessResponse()) {
                CodeAndMessage codeAndMessage = de.getCodeAndMessage();
                T response = createResponse.get();
                response.setCode(codeAndMessage.getCode());
                response.setMessage(codeAndMessage.getMessage());
                return response;
            }
            log.error("Error sending {} {}", taskName, de);
            throw de;
        }
    }
}

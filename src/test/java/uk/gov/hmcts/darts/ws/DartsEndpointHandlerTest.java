package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DartsEndpointHandlerTest {

    private CodeAndMessage assertMessage = CodeAndMessage.NOT_FOUND_COURTHOUSE;

    @Test
    public void testEndpointExecution() {
        DARTSResponse response = new DartsEndpointHandler().makeAPICall(
            "",
            this::executeWithCourtHouseError,
            () -> new DARTSResponse()
        );

        Assertions.assertEquals(assertMessage.getCode(), response.getCode());
        Assertions.assertEquals(assertMessage.getMessage(), response.getMessage());
    }

    private DARTSResponse executeWithCourtHouseError() {
        throw new DartsException(null, assertMessage);
    }
}

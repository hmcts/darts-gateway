package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.exceptions.DartsException;

class DartsEndpointHandlerTest {

    private static final CodeAndMessage ASSERT_MESSAGE = CodeAndMessage.NOT_FOUND_COURTHOUSE;

    @Test
    void testEndpointExecution() {
        DARTSResponse response = new DartsEndpointHandler().makeAPICall(
            "",
            this::executeWithCourtHouseError,
            () -> new DARTSResponse()
        );

        Assertions.assertEquals(ASSERT_MESSAGE.getCode(), response.getCode());
        Assertions.assertEquals(ASSERT_MESSAGE.getMessage(), response.getMessage());
    }

    private DARTSResponse executeWithCourtHouseError() {
        throw new DartsException(ASSERT_MESSAGE);
    }
}

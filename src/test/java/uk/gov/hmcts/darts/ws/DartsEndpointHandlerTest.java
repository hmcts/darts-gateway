package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.exceptions.DartsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DartsEndpointHandlerTest {

    private static final CodeAndMessage ASSERT_MESSAGE = CodeAndMessage.NOT_FOUND_COURTHOUSE;

    @Test
    void testEndpointExecution() {
        DartsException exception = assertThrows(DartsException.class, () -> new DartsEndpointHandler().makeAPICall(
            "",
            this::executeWithCourtHouseError,
            DARTSResponse::new
        ));
        assertThat(exception.getCodeAndMessage()).isEqualTo(ASSERT_MESSAGE);
    }

    private DARTSResponse executeWithCourtHouseError() {
        throw new DartsException(ASSERT_MESSAGE);
    }
}

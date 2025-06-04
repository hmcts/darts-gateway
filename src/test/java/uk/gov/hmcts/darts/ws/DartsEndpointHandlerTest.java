package uk.gov.hmcts.darts.ws;

import com.synapps.moj.dfs.response.DARTSResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.hmcts.darts.common.exceptions.DartsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DartsEndpointHandlerTest {
    ;

    @ParameterizedTest
    @EnumSource(value = CodeAndMessage.class, names = "ERROR", mode = EnumSource.Mode.INCLUDE)
    void testEndpointExecution_whenUsingACodeANdMessageWIthIsSuccessFalse_shouldThrowError(CodeAndMessage codeAndMessage) {
        DartsException exception = assertThrows(DartsException.class, () -> new DartsEndpointHandler().makeAPICall(
            "",
            () -> executeWithCourtHouseError(codeAndMessage),
            DARTSResponse::new
        ));
        assertThat(exception.getCodeAndMessage()).isEqualTo(codeAndMessage);
    }

    @ParameterizedTest
    @EnumSource(value = CodeAndMessage.class, names = "ERROR", mode = EnumSource.Mode.EXCLUDE)
    void testEndpointExecution_whenUsingACodeANdMessageWIthIsSuccessTrue_shouldNotThrowErrorButInsteadMapResponseFields(CodeAndMessage codeAndMessage) {
        DARTSResponse response = new DartsEndpointHandler().makeAPICall(
            "",
            () -> executeWithCourtHouseError(codeAndMessage),
            DARTSResponse::new
        );
        assertThat(response.getCode()).isEqualTo(codeAndMessage.getCode());
        assertThat(response.getMessage()).isEqualTo(codeAndMessage.getMessage());
    }

    private DARTSResponse executeWithCourtHouseError(CodeAndMessage codeAndMessage) {
        throw new DartsException(codeAndMessage);
    }
}

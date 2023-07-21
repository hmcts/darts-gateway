package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class DartsResponseUtilsTest {

    private final DartsResponseUtils dartsResponseUtils = new DartsResponseUtils();

    @Test
    void createsDartsResponseFromCodeAndMessage() {
        var dartsResponse = dartsResponseUtils.createDartsResponseMessage(CodeAndMessage.OK);

        assertThat(dartsResponse.getCode()).isEqualTo("200");
        assertThat(dartsResponse.getMessage()).isEqualTo("OK");
    }

    @Test
    void createsDartsResponseFromDartsValidationException() {
        var validationException = new DartsValidationException(new RuntimeException());
        var dartsResponse = dartsResponseUtils.createDartsResponseMessage(validationException);

        assertThat(dartsResponse.getCode()).isEqualTo("400");
        assertThat(dartsResponse.getMessage()).isEqualTo("Invalid XML Document");
    }

    @Test
    void createsCourtLogResponseFromCodeAndMessage() {
        var getCourtLogResponse = dartsResponseUtils.createCourtLogResponse(CodeAndMessage.OK);

        assertThat(getCourtLogResponse.getCode()).isEqualTo("200");
        assertThat(getCourtLogResponse.getMessage()).isEqualTo("OK");
    }

    @Test
    void createsCourtLogResponseFromDartsValidationException() {
        var validationException = new DartsValidationException(new RuntimeException());
        var getCourtLogResponse = dartsResponseUtils.createCourtLogResponse(validationException);

        assertThat(getCourtLogResponse.getCode()).isEqualTo("400");
        assertThat(getCourtLogResponse.getMessage()).isEqualTo("Invalid XML Document");
    }

    @Test
    void defaultsTo500ForUnknownException() {
        var dartsResponse = dartsResponseUtils.createDartsResponseMessage(new RuntimeException());

        assertThat(dartsResponse.getCode()).isEqualTo("500");
        assertThat(dartsResponse.getMessage()).isNull();
    }
}

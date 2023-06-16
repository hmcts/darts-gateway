package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.exceptions.DartsValidationException;

import static org.assertj.core.api.Assertions.assertThat;

class DartsResponseUtilsTest {

    private final DartsResponseUtils dartsResponseUtils = new DartsResponseUtils();

    @Test
    void createsDartsResponseFromCodeAndMessage() {
        var dartsResponse = dartsResponseUtils.createResponseMessage(CodeAndMessage.OK);

        assertThat(dartsResponse.getCode()).isEqualTo("200");
        assertThat(dartsResponse.getMessage()).isEqualTo("OK");
    }

    @Test
    void createsDartsResponseFromDartsValidationException() {
        var validationException = new DartsValidationException(new RuntimeException());
        var dartsResponse = dartsResponseUtils.createResponseMessage(validationException);

        assertThat(dartsResponse.getCode()).isEqualTo("400");
        assertThat(dartsResponse.getMessage()).isEqualTo("Invalid XML Document");
    }

    @Test
    void defaultsTo500ForUnknownException() {
        var dartsResponse = dartsResponseUtils.createResponseMessage(new RuntimeException());

        assertThat(dartsResponse.getCode()).isEqualTo("500");
        assertThat(dartsResponse.getMessage()).isNull();
    }
}

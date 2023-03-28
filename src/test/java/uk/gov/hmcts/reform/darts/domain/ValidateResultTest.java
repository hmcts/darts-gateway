package uk.gov.hmcts.reform.darts.domain;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateResultTest {

    private static final String TEST_ERROR_MESSAGE = "Test error message";

    @Test
    void testGetters() {
        ValidateResult validateResult = new ValidateResult(true, TEST_ERROR_MESSAGE);

        assertTrue(validateResult.isValid(), "ValidateResult does not have expected valid state");
        assertEquals(
            TEST_ERROR_MESSAGE,
            validateResult.getError(),
            "ValidateResult does not have expected error message"
        );
    }
}

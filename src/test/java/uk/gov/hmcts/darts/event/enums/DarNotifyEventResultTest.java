package uk.gov.hmcts.darts.event.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DarNotifyEventResultTest {

    @Test
    void shouldReturnDarNotifyEventResultWhenResultCodeValid() {
        DarNotifyEventResult darNotifyEventResult = DarNotifyEventResult.OK;
        assertEquals(darNotifyEventResult, DarNotifyEventResult.valueOfResult(0));
        assertEquals("OK", darNotifyEventResult.getMessage());

        darNotifyEventResult = DarNotifyEventResult.MALFORMED;
        assertEquals(darNotifyEventResult, DarNotifyEventResult.valueOfResult(1));
        assertEquals("XML malformed", darNotifyEventResult.getMessage());

        darNotifyEventResult = DarNotifyEventResult.NO_DESTINATION_DATA;
        assertEquals(darNotifyEventResult, DarNotifyEventResult.valueOfResult(2));
        assertEquals("No destination data", darNotifyEventResult.getMessage());

        darNotifyEventResult = DarNotifyEventResult.WRONG_DESTINATION;
        assertEquals(darNotifyEventResult, DarNotifyEventResult.valueOfResult(3));
        assertEquals("Wrong destination", darNotifyEventResult.getMessage());

        darNotifyEventResult = DarNotifyEventResult.NO_MATCHING_EVENT;
        assertEquals(darNotifyEventResult, DarNotifyEventResult.valueOfResult(4));
        assertEquals("No matching event", darNotifyEventResult.getMessage());

        darNotifyEventResult = DarNotifyEventResult.OTHER_ERROR;
        assertEquals(darNotifyEventResult, DarNotifyEventResult.valueOfResult(5));
        assertEquals("Other error", darNotifyEventResult.getMessage());
    }

    @Test
    void shouldReturnNullWhenResultCodeUnknown() {
        assertNull(DarNotifyEventResult.valueOfResult(6));
    }

}

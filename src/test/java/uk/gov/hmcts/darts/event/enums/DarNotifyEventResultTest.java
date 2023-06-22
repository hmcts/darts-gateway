package uk.gov.hmcts.darts.event.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DarNotifyEventResultTest {

    @Test
    void shouldReturnDarNotifyEventResultWhenResultCodeValid() {
        assertEquals(DarNotifyEventResult.OK, DarNotifyEventResult.valueOfResult(Integer.valueOf(0)));
        assertEquals(DarNotifyEventResult.OK, DarNotifyEventResult.valueOfResult(0));
        assertEquals(DarNotifyEventResult.MALFORMED, DarNotifyEventResult.valueOfResult(1));
        assertEquals(DarNotifyEventResult.NO_DESTINATION_DATA, DarNotifyEventResult.valueOfResult(2));
        assertEquals(DarNotifyEventResult.WRONG_DESTINATION, DarNotifyEventResult.valueOfResult(3));
        assertEquals(DarNotifyEventResult.NO_MATCHING_EVENT, DarNotifyEventResult.valueOfResult(4));
        assertEquals(DarNotifyEventResult.OTHER_ERROR, DarNotifyEventResult.valueOfResult(5));
    }

    @Test
    void shouldReturnNullWhenResultCodeUnknown() {
        assertNull(DarNotifyEventResult.valueOfResult(6));
    }

}

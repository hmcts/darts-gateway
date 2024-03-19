package uk.gov.hmcts.darts.event.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.MALFORMED;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.NO_DESTINATION_DATA;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.NO_MATCHING_EVENT;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.OK;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.OTHER_ERROR;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.WRONG_DESTINATION;
import static uk.gov.hmcts.darts.event.enums.DarNotifyEventResult.findByResult;

class DarNotifyEventResultTest {

    @Test
    void returnsCorrectEnum() {
        assertThat(findByResult(0))
            .isEqualTo(OK)
            .hasFieldOrPropertyWithValue("message", "OK");

        assertThat(findByResult(1))
            .isEqualTo(MALFORMED)
            .hasFieldOrPropertyWithValue("message", "XML malformed");

        assertThat(findByResult(2))
            .isEqualTo(NO_DESTINATION_DATA)
            .hasFieldOrPropertyWithValue("message", "No destination data");

        assertThat(findByResult(3))
            .isEqualTo(WRONG_DESTINATION)
            .hasFieldOrPropertyWithValue("message", "Wrong destination");

        assertThat(findByResult(4))
            .isEqualTo(NO_MATCHING_EVENT)
            .hasFieldOrPropertyWithValue("message", "No matching event");

        assertThat(findByResult(5))
            .isEqualTo(OTHER_ERROR)
            .hasFieldOrPropertyWithValue("message", "Other error");
    }

}

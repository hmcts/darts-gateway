package uk.gov.hmcts.darts.event.service.impl;

import org.junit.jupiter.api.Test;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.courtservice.events.DartsEvent.CaseNumbers;

import static java.math.BigInteger.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

class EventRequestMapperTest {

    private final EventRequestMapper mapper = new EventRequestMapper();

    @Test
    void mapsEvents() {
        var legacyDartsEvent = new DartsEvent();
        var caseNumbers = new CaseNumbers();
        caseNumbers.caseNumber = list("1", "2", "3");
        legacyDartsEvent.setCaseNumbers(caseNumbers);
        legacyDartsEvent.setEventText("some-event-text");
        legacyDartsEvent.setD(ONE);
        legacyDartsEvent.setH(ONE);
        legacyDartsEvent.setM(ONE);
        legacyDartsEvent.setS(ONE);
        legacyDartsEvent.setY(ONE);
        legacyDartsEvent.setMIN(ONE);
        legacyDartsEvent.setID(ONE);
        legacyDartsEvent.setCourtHouse("some-court-house");
        legacyDartsEvent.setCourtRoom("some-court-room");

        uk.gov.hmcts.darts.model.event.DartsEvent eventRequest = mapper.toNewApi(
            legacyDartsEvent, "some-message-id", "some-type", "some-sub-type");

        assertThat(eventRequest.getEventId()).isEqualTo(String.valueOf(ONE));
        assertThat(eventRequest.getCaseNumbers()).containsExactly("1", "2", "3");
        assertThat(eventRequest.getEventText()).isEqualTo("some-event-text");
        assertThat(eventRequest.getCourthouse()).isEqualTo("some-court-house");
        assertThat(eventRequest.getCourtroom()).isEqualTo("some-court-room");
        assertThat(eventRequest.getDateTime()).isEqualTo("0001-01-01T01:01:01Z");
    }
}

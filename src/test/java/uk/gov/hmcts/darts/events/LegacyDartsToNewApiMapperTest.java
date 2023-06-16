package uk.gov.hmcts.darts.events;

import org.junit.jupiter.api.Test;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.courtservice.events.DartsEvent.CaseNumbers;

import static java.math.BigInteger.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

@SuppressWarnings("AvoidStarImport")
class LegacyDartsToNewApiMapperTest {

    final LegacyDartsToNewApiMapper mapper = new LegacyDartsToNewApiMapper();

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

        EventRequest eventRequest = mapper.toNewApi(
            legacyDartsEvent, "some-message-id", "some-type", "some-sub-type");

        assertThat(eventRequest.caseNumbers()).containsExactly("1", "2", "3");
        assertThat(eventRequest.eventText()).isEqualTo("some-event-text");
        assertThat(eventRequest.courtHouse()).isEqualTo("some-court-house");
        assertThat(eventRequest.courtRoom()).isEqualTo("some-court-room");
        assertThat(eventRequest.dateTime()).isEqualTo("0001-01-01T01:01:01");
    }
}

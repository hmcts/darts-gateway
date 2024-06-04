package uk.gov.hmcts.darts.event.service.impl;

import org.junit.jupiter.api.Test;
import uk.gov.courtservice.events.DartsEvent;
import uk.gov.courtservice.events.DartsEvent.CaseNumbers;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.junit.jupiter.api.Assertions.assertNull;

class EventRequestMapperTest {

    private final EventRequestMapper mapper = new EventRequestMapper();

    @Test
    void mapsEventsGmt() {
        DartsEvent dartsEvent = eventTestDataSetup(ONE);

        uk.gov.hmcts.darts.model.event.DartsEvent eventRequest = mapper.toNewApi(
            dartsEvent, "some-message-id", "some-type", "some-sub-type");

        assertThat(eventRequest.getEventId()).isEqualTo(String.valueOf(ONE));
        assertThat(eventRequest.getCaseNumbers()).containsExactly("1", "2", "3");
        assertThat(eventRequest.getEventText()).isEqualTo("some-event-text");
        assertThat(eventRequest.getCourthouse()).isEqualTo("some-court-house");
        assertThat(eventRequest.getCourtroom()).isEqualTo("some-court-room");
        assertThat(eventRequest.getDateTime()).isEqualTo("2024-01-01T10:01:01Z");
        assertNull(eventRequest.getRetentionPolicy());
    }

    @Test
    void mapsEventsBst() {
        DartsEvent dartsEvent = eventTestDataSetup(BigInteger.valueOf(6));

        uk.gov.hmcts.darts.model.event.DartsEvent eventRequest = mapper.toNewApi(
            dartsEvent, "some-message-id", "some-type", "some-sub-type");

        assertThat(eventRequest.getEventId()).isEqualTo(String.valueOf(ONE));
        assertThat(eventRequest.getCaseNumbers()).containsExactly("1", "2", "3");
        assertThat(eventRequest.getEventText()).isEqualTo("some-event-text");
        assertThat(eventRequest.getCourthouse()).isEqualTo("some-court-house");
        assertThat(eventRequest.getCourtroom()).isEqualTo("some-court-room");
        assertThat(eventRequest.getDateTime()).isEqualTo("2024-06-01T09:01:01Z");
        assertNull(eventRequest.getRetentionPolicy());
    }

    @Test
    void mapRetentionPolicy() {
        DartsEvent dartsEvent = eventTestDataSetup(BigInteger.valueOf(6));

        DartsEvent.RetentionPolicy retentionPolicy = new DartsEvent.RetentionPolicy();
        retentionPolicy.setCaseRetentionFixedPolicy("3");
        retentionPolicy.setCaseTotalSentence("1Y2M0D");
        dartsEvent.setRetentionPolicy(retentionPolicy);

        uk.gov.hmcts.darts.model.event.DartsEvent eventRequest = mapper.toNewApi(
            dartsEvent, "some-message-id", "some-type", "some-sub-type");

        assertThat(eventRequest.getRetentionPolicy().getCaseRetentionFixedPolicy()).isEqualTo("3");
        assertThat(eventRequest.getRetentionPolicy().getCaseTotalSentence()).isEqualTo("1Y2M0D");
    }

    private DartsEvent eventTestDataSetup(BigInteger month) {
        var dartsEvent = new DartsEvent();
        var caseNumbers = new CaseNumbers();
        caseNumbers.caseNumber = list("1", "2", "3");
        dartsEvent.setCaseNumbers(caseNumbers);
        dartsEvent.setEventText("some-event-text");
        dartsEvent.setY(BigInteger.valueOf(2024));
        dartsEvent.setM(month);
        dartsEvent.setD(ONE);
        dartsEvent.setH(BigInteger.valueOf(10));
        dartsEvent.setS(ONE);
        dartsEvent.setMIN(ONE);
        dartsEvent.setID(ONE);
        dartsEvent.setCourtHouse("some-court-house");
        dartsEvent.setCourtRoom("some-court-room");

        return dartsEvent;
    }
}

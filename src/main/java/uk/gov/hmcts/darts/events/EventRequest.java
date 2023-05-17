package uk.gov.hmcts.darts.events;

import java.time.LocalDateTime;
import java.util.List;

// This will be moved to the API repo or a library
public record EventRequest(
    String type,
    String subType,
    String courtHouse,
    String courtRoom,
    List<String> caseNumbers,
    String eventText,
    LocalDateTime dateTime) {
}

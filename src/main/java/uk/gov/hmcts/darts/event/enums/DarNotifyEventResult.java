package uk.gov.hmcts.darts.event.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
public enum DarNotifyEventResult {

    OK(0, "OK"),
    MALFORMED(1, "XML malformed"),
    NO_DESTINATION_DATA(2, "No destination data"),
    WRONG_DESTINATION(3, "Wrong destination"),
    NO_MATCHING_EVENT(4, "No matching event"),
    OTHER_ERROR(5, "Other error");

    private final int result;
    private final String message;

    public static DarNotifyEventResult findByResult(int result) {
        return stream(values())
            .filter(r -> r.getResult() == result)
            .findFirst().orElse(null);
    }

}

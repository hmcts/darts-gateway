package uk.gov.hmcts.darts.events.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum DarNotifyEventResult {

    OK(0, "OK"),
    MALFORMED(1, "XML malformed"),
    NO_DESTINATION_DATA(2, "No destination data"),
    WRONG_DESTINATION(3, "Wrong destination"),
    NO_MATCHING_EVENT(4, "No matching event"),
    OTHER_ERROR(5, "Other error");

    private static final Map<Integer, DarNotifyEventResult> BY_RESULT = new HashMap<>();

    static {
        for (DarNotifyEventResult e : values()) {
            BY_RESULT.put(e.result, e);
        }
    }

    private final int result;
    private final String message;

    public static DarNotifyEventResult valueOfResult(int result) {
        return BY_RESULT.get(result);
    }

}

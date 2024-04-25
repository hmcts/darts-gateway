package uk.gov.hmcts.darts.event.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
@Slf4j
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
        return stream(DarNotifyEventResult.values())
            .filter(r -> r.getResult() == result)
            .findFirst().orElseGet(() -> {
                log.warn("unknown DarNotifyEventResult {}", result);
                return null;
            });
    }

}

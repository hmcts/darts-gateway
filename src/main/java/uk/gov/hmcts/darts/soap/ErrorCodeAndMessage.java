package uk.gov.hmcts.darts.soap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodeAndMessage {

    ERROR("500", null),
    INVALID_XML("400", "Invalid XML Document"),
    NOT_FOUND_COURTHOUSE("404", "Courthouse Not Found"),
    NOT_FOUND_HANLDER("404", "Handler Not Found"),
    AUDIO_TOO_LARGE("404", "Audio Too Large"),
    OK("200", "OK");

    private final String code;
    private final String message;

}

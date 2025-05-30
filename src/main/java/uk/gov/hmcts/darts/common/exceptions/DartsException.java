package uk.gov.hmcts.darts.common.exceptions;

import lombok.Getter;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@Getter
public class DartsException extends RuntimeException {
    private final CodeAndMessage codeAndMessage;

    public DartsException(Throwable cause, String message, CodeAndMessage codeAndMessage) {
        super(message, cause);
        this.codeAndMessage = codeAndMessage;
    }

    public DartsException(Throwable cause, CodeAndMessage codeAndMessage) {
        this(cause, codeAndMessage.getMessage(), codeAndMessage);
    }

    public DartsException(String message, CodeAndMessage codeAndMessage) {
        this(null, message, codeAndMessage);
    }

    public DartsException(CodeAndMessage codeAndMessage) {
        this(codeAndMessage.getMessage(), codeAndMessage);
    }

    public boolean hasCodeAndMessage() {
        return this.codeAndMessage != null;
    }
}

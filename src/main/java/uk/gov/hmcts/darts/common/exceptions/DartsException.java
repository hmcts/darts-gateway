package uk.gov.hmcts.darts.common.exceptions;

import uk.gov.hmcts.darts.ws.CodeAndMessage;

public class DartsException extends RuntimeException {
    private final CodeAndMessage codeAndMessage;

    public DartsException(Throwable cause, CodeAndMessage codeAndMessage) {
        super(cause);
        this.codeAndMessage = codeAndMessage;
    }

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public DartsException(CodeAndMessage codeAndMessage) {
        this.codeAndMessage = codeAndMessage;
    }

    public DartsException(String cause, CodeAndMessage codeAndMessage) {
        super(cause);
        this.codeAndMessage = codeAndMessage;
    }

    public boolean hasCodeAndMessage() {
        return codeAndMessage != null;
    }

    public CodeAndMessage getCodeAndMessage() {
        return codeAndMessage;
    }
}

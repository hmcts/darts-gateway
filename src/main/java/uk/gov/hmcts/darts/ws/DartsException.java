package uk.gov.hmcts.darts.ws;

public class DartsException extends RuntimeException {
    private final CodeAndMessage codeAndMessage;

    public DartsException(Throwable cause, CodeAndMessage codeAndMessage) {
        super(cause);
        this.codeAndMessage = codeAndMessage;
    }

    public CodeAndMessage getCodeAndMessage() {
        return codeAndMessage;
    }
}

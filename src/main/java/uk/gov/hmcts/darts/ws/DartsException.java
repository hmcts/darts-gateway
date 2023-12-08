package uk.gov.hmcts.darts.ws;

public class DartsException extends RuntimeException {
    private final CodeAndMessage codeAndMessage;

    public DartsException(Throwable cause, CodeAndMessage codeAndMessage) {
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

package uk.gov.hmcts.darts.ws;

public class DartsException extends RuntimeException {
    private CodeAndMessage codeAndMessage;

    public DartsException(Throwable cause, CodeAndMessage codeAndMessage) {
        super(cause);
        this.codeAndMessage = codeAndMessage;
    }

    public DartsException(String message) {
        super(message);
    }

    public boolean hasCodeAndMessage() {
        return codeAndMessage != null;
    }

    public CodeAndMessage getCodeAndMessage() {
        return codeAndMessage;
    }
}

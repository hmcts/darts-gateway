package uk.gov.hmcts.darts.soap;

public class DartsException extends RuntimeException {
    private final ErrorCodeAndMessage errorCodeAndMessage;

    public DartsException(Throwable cause, ErrorCodeAndMessage errorCodeAndMessage) {
        super(cause);
        this.errorCodeAndMessage = errorCodeAndMessage;
    }

    public ErrorCodeAndMessage getCodeAndMessage() {
        return errorCodeAndMessage;
    }
}

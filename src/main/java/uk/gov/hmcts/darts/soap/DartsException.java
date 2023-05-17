package uk.gov.hmcts.darts.soap;

public class DartsException extends RuntimeException {
    private final DartsResponseCodeAndMessage dartsResponseCodeAndMessage;

    public DartsException(Throwable cause, DartsResponseCodeAndMessage dartsResponseCodeAndMessage) {
        super(cause);
        this.dartsResponseCodeAndMessage = dartsResponseCodeAndMessage;
    }

    public DartsResponseCodeAndMessage getCodeAndMessage() {
        return dartsResponseCodeAndMessage;
    }
}

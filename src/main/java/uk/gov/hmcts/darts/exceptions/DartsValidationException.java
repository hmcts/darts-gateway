package uk.gov.hmcts.darts.exceptions;

public class DartsValidationException extends RuntimeException {

    public DartsValidationException(Throwable throwable) {
        super(throwable);
    }
}

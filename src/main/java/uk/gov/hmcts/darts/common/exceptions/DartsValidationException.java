package uk.gov.hmcts.darts.common.exceptions;

public class DartsValidationException extends RuntimeException {

    public DartsValidationException(Throwable throwable) {
        super(throwable);
    }
}

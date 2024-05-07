package uk.gov.hmcts.darts.common.util;

/**
 * A generic validation interface.
 */
public interface Validator<T> {

    void validate(T validatable);

}

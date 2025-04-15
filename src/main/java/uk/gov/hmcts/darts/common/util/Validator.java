package uk.gov.hmcts.darts.common.util;

/**
 * A generic validation interface.
 */
@FunctionalInterface
public interface Validator<T> {
    void validate(T validatable);
}

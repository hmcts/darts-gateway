package uk.gov.hmcts.reform.darts.validate.services;

import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;

public interface ValidateService {

    /**
     * Perform validation on content.
     * @param content The content to perform validation on
     * @return A ValidationResult object containing the result of the validation
     */
    ValidateResult validateContent(String content);
}

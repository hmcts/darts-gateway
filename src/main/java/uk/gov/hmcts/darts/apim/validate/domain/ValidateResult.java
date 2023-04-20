package uk.gov.hmcts.darts.apim.validate.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidateResult {

    private boolean valid;

    private String error;
}

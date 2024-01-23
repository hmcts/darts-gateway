package uk.gov.hmcts.darts.cache.token.component;

public interface TokenValidator {

    boolean validate(String accessToken);
}

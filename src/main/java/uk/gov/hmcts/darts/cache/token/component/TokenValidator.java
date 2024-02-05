package uk.gov.hmcts.darts.cache.token.component;

@FunctionalInterface
public interface TokenValidator {

    /**
     * validates a token.
     * @param validateUsingExpiryOffset We can expire slightly before the actual token expiry based on sysTem configuration
     * @param accessToken The token to validate
     * @return Whether validation had succeeded or not
     */
    boolean validate(boolean validateUsingExpiryOffset, String accessToken);
}

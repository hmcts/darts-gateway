package uk.gov.hmcts.darts.cache.token.component;

import uk.gov.hmcts.darts.cache.token.service.Token;

import java.util.function.BiPredicate;

public interface TokenValidator extends BiPredicate<Token.TokenExpiryEnum, String> {

    /**
     * validates a token.
     * @param validateUsingExpiryOffset We can expire slightly before the actual token expiry based on sysTem configuration
     * @param accessToken The token to validate
     * @return Whether validation had succeeded or not
     */
    @Override
    boolean test(Token.TokenExpiryEnum validateUsingExpiryOffset, String accessToken);
}

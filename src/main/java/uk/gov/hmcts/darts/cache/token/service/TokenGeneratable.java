package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

/**
 * An interface that allows us to generate tokens.
 */
public interface TokenGeneratable {

    /**
     * generates a token based on the service context credentials.
     * @return The credentials in token form
     * @throws CacheException Any cache related problems whilst getting the token
     */
    Token createToken(ServiceContext context);

    /**
     * gets a random token string and gets it in token form.
     * @return The token
     */
    Token getToken(String token);
}

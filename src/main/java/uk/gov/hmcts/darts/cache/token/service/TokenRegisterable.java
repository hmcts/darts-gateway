package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.util.Optional;

/**
 * This is a token registration interface that represents a token cache that allows us to set and get
 * values {@link uk.gov.hmcts.darts.cache.token.service.value.CacheValue} from the cache.
 */
public interface TokenRegisterable {

    String CACHE_PREFIX = "darts-gateway-token-service";

    /**
     * Stores a cache value into the cache.
     * @return The token in which the cache value is mapped against
     */
    Optional<Token> store(CacheValue value);

    /**
     * Stores a cache value into the store. If the cache value is already mapped into the cache we can
     * return the same id if we choose to.
     * This method should ensure the token is valid
     * (whatever that may mean) before it is returned. Clients can validate the tokens at any time thereafter
     * by calling {@link Token#validate()}
     * @param reuseTokenIfPossible If set to true we reuse the token else we create a new one
     */
    Optional<Token> store(CacheValue value, Boolean reuseTokenIfPossible);

    /**
     * This method takes the service context and works out whether it needs to generate a new token or not.
     * If a token does exists but it is invalid then a new token is expected to be generated
     *
     * @param context The context to store
     * @param reuseTokenIfPossible Enables reuse of a preexisting token if found. If false a new token is always generated for the same service context.
     *
     * @return The token to be returned.
     */
    Optional<Token> store(ServiceContext context, Boolean reuseTokenIfPossible);

    /**
     * This method takes the service context to generate a new token.
     * @param context The service context to store
     * @return The token to be returned
     */
    Optional<Token> store(ServiceContext context);

    /**
     * lookup the token value.
     * @param holder The token
     * @return The value. If the token could not be found or the existing token is
     *      found but it is invalid (whatever that means) we should return an empty value (no exception should be thrown).
     */
    Optional<CacheValue> lookup(Token holder);

    /**
     * This evicts a token from the cache. No errors are expected. After this call {@link #lookup(Token)} should return empty,
     * except for the  case where we are sharing tokens
     * @param token The token
     */
    void evict(Token token);


    /**
     * Gets a token.
     * @param token The token in string form
     * @return The token
     */
    Token getToken(String token);

    /**
     * creates a value to be stored.
     * @param serviceContext The service context
     * @return The value sub type to be stored
     */
    CacheValue createValue(ServiceContext serviceContext);

}

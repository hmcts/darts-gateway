package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * An abstract redis based cache implementation that is driven off the spring configuration defined
 * {@link uk.gov.hmcts.darts.cache.token.config.CacheProperties}.
 * This implementation ensures that Redis locking is in place for token sharing:-
 * - When sharing tokens we minimise communicating with the idp
 * - Keep token allocation as atomic as possible between clients to avoid inconsistent redis data
 * By default when allocating shared tokens we expire tokens x minutes before they need to be. This ensures the returned tokens have
 * some life in them for the calling client to then leverage
 * This implementation supports the use of cache values that implement {@link uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue}
 * On lookup of a cache value we ensure that the internal token is valid before returning.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractTokenCache implements TokenRegisterable {

    private final RedisTemplate<String, Object> redisTemplate;

    private final LockRegistry lockRegistry;

    protected final CacheProperties properties;


    private Duration secondsToExpire() {
        return Duration.of(properties.getEntryTimeToIdleSeconds(), ChronoUnit.SECONDS);
    }

    @PostConstruct
    public void postConstruct() {
        redisTemplate.setEnableTransactionSupport(true);
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S6809")
    public Optional<Token> store(CacheValue value) throws CacheException {
        return store(value, null);
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S6809")
    public Optional<Token> store(CacheValue value, Boolean reuseTokenIfPossible) throws CacheException {
        return createNewTokenOrReuseExistingToken(value, reuseTokenIfPossible, true);
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S6809")
    public Optional<Token> store(ServiceContext context) throws CacheException {
        return store(context, false);
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S6809")
    public Optional<Token> store(ServiceContext context, Boolean reuseTokenIfPossible) throws CacheException {

        String sharedId = getIdForServiceContext(context);
        if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            Object sharedToken = redisTemplate.opsForValue().get(sharedId);
            Token token = Token.readToken((String) sharedToken, properties.isMapTokenToSession(),
                                          getValidateToken()
            );
            Optional<CacheValue> value = lookup(token);
            if (value.isPresent()) {
                // store into redis without validation of the associated token
                return Optional.of(createNewTokenOrReuseExistingToken(value.get(), reuseTokenIfPossible, false).get());
            } else {
                log.debug("Not looking up shared token either turned off or not  forced");
                return Optional.of(store(createValue(context), reuseTokenIfPossible).get());
            }
        } else {
            log.debug("Not looking up shared token either turned off or not forced");
            return Optional.of(store(createValue(context), reuseTokenIfPossible).get());
        }
    }

    private boolean reuseTokenBasedOnCredentials(Boolean reuseTokenIfPossible) {
        return properties.isShareTokenForSameCredentials()
            || BooleanUtils.isTrue(reuseTokenIfPossible);
    }


    @Override
    @Transactional
    @SuppressWarnings("java:S6809")
    public Optional<CacheValue> lookup(Token tokenToLookup) throws CacheException {
        log.debug("Looking up the token");

        Optional<CacheValue> val = read(tokenToLookup);

        boolean tokenIsValid = tokenToLookup.validate();
        if (val.isPresent() && !tokenIsValid) {
            evict(tokenToLookup);
            val = Optional.empty();
            log.debug("Token manually removed as it is no longer valid");
        } else {
            if (val.isPresent() && val.get() instanceof DownstreamTokenisableValue downstreamToken && downstreamToken.doesRequireRefresh()) {
                log.debug("Token cache value needs refreshing");
                downstreamToken.performRefresh();
                log.debug("Token cache value has been refreshed");
            }

            if (val.isPresent()) {
                redisTemplate.opsForValue().set(tokenToLookup.getKey(), val.get(), secondsToExpire());
                redisTemplate.expire(tokenToLookup.getKey(), secondsToExpire());
            }
        }

        log.debug("Returning found value");
        return val;
    }

    protected abstract CacheValue getValue(CacheValue holder) throws CacheException;

    protected abstract TokenValidator getValidateToken();

    @Override
    public Token getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession(), getValidateToken());
    }

    @Override
    @Transactional
    public void evict(Token holder) throws CacheException {
        log.debug("Evicting the token");

        if (!properties.isShareTokenForSameCredentials()) {

            redisTemplate.delete(holder.getKey());

            log.debug("Evicted the token");
        } else {
            log.debug("Token was not evicted as it can be shared. The token expiration timeout is still applicable");
        }
    }

    private Optional<CacheValue> read(Token holder) {
        return getRefreshValueWithResetExpiration(holder);
    }

    private Optional<Token> read(CacheValue holder) {
        return getTokenValueWithResetExpiration(holder);
    }

    private Optional<CacheValue> getRefreshValueWithResetExpiration(Token token) {
        Object value = redisTemplate.opsForValue().get(token.getKey());

        if (value != null) {
            return Optional.of(getValue((CacheValue) value));
        }

        return Optional.empty();
    }

    private Optional<Token> getTokenValueWithResetExpiration(CacheValue holder) {
        Object value = redisTemplate.opsForValue().get(holder.getSharedKey());

        if (value != null) {
            return Optional.of(Token.readToken((String) value, properties.isMapTokenToSession(), getValidateToken()));
        }

        return Optional.empty();
    }

    private boolean getValidTokenFromCache(Token token, boolean validateToken) {
        log.debug("Found the token in the cache");
        if (validateToken && !token.validate(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY)) {
            return false;
        }

        return true;
    }

    /**
     * This function is key for storing to redis. It takes a service context and if reuse of token is required looks
     * for the redis token and validates it (depending on the token representation). If the token is not valid then we aquire
     * a new token and store in redis. If we do not require reuse a new token is generated each time.
     * @param valueToBeCached The value to cache.
     * @param reuseTokenIfPossible Do we want to use a shared token if we can.
     * @param validateToken Do we wish to validate the token if one exists and sharing is required.
     * @return The token to use. This maybe a shared token or a branch new one.
     */
    @SuppressWarnings("java:S6809")
    private Optional<Token> createNewTokenOrReuseExistingToken(CacheValue valueToBeCached,
                                                               Boolean reuseTokenIfPossible, boolean validateToken) throws CacheException {

        log.debug("Storing the supplied value");

        Optional<Token> tokenToUse = Optional.empty();

        // if we have found a token, but it is invalid then invalidate it and force a new token to be created
        if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            log.debug("Looking up token in cache");

            // validate the token
            tokenToUse = read(valueToBeCached);

            if (tokenToUse.isPresent()) {
                if (!getValidTokenFromCache(tokenToUse.get(), validateToken)) {
                    tokenToUse = Optional.empty();
                    log.debug("Cached token is invalid. Proceeding to create new token");
                }
            } else {
                log.debug("Token not found in cache");
            }

            if (tokenToUse.isEmpty()) {
                tokenToUse = Optional.of(createTokenWithSharedRedisLock(valueToBeCached));
            }
        } else {
            tokenToUse = Optional.of(createToken(valueToBeCached.getServiceContext()));
        }

        Token consumerToken = tokenToUse.get();
        CacheValue cachedValueIncludingdartsApiToken = valueToBeCached;

        storeToRedis(consumerToken, cachedValueIncludingdartsApiToken);

        log.debug("Token value stored in cache");

        return tokenToUse;
    }

    private void storeToRedis(Token consumerToken, CacheValue cachedValueIncludingdartsApiToken) {
        // now update the redis ids with the shared token details as well as the token for the request
        Duration secondsToExpire = secondsToExpire();

        // map the token to the cache value
        redisTemplate.opsForValue().set(consumerToken.getKey(), cachedValueIncludingdartsApiToken, secondsToExpire);

        // map the cache value shared key to the token - This is the shared id for the context registry and acts as a reverse lookup when
        // shared tokens are required
        redisTemplate.opsForValue().set(cachedValueIncludingdartsApiToken.getSharedKey(), consumerToken.getTokenString(), secondsToExpire);

        // now explicitly set the time to idle on the keys
        redisTemplate.expire(consumerToken.getKey(), secondsToExpire());
        redisTemplate.expire(cachedValueIncludingdartsApiToken.getSharedKey(), secondsToExpire());
    }

    private Token createTokenWithSharedRedisLock(CacheValue value) {
        // ensure that one token is acquired according to the shared token id. This prevents up from overwhelming
        // the underlying idp
        CacheLockableUnitOfWork distributedLockWork = new CacheLockableUnitOfWork(lockRegistry);
        return distributedLockWork.execute(() -> {

            // read the token again one at a time to skip token creation
            Optional<Token> foundToken = read(value);

            // if the token does not exist then create the token
            return foundToken.orElseGet(() -> createToken(value.getServiceContext()));
        }, value.getSharedKey());
    }

    protected abstract Token createToken(ServiceContext context) throws CacheException;

    protected abstract String getIdForServiceContext(ServiceContext context) throws CacheException;

}

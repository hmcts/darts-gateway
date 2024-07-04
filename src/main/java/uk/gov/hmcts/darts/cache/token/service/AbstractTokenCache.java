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
import uk.gov.hmcts.darts.cache.token.enums.TokenStatus;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.model.TokenWithStatus;
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
    @SuppressWarnings({"java:S6809", "PMD.AvoidUncheckedExceptionsInSignatures"})
    public Optional<Token> store(CacheValue value) throws CacheException {
        return store(value, null);
    }

    @Override
    @Transactional
    @SuppressWarnings({"java:S6809", "PMD.AvoidUncheckedExceptionsInSignatures"})
    public Optional<Token> store(CacheValue value, Boolean reuseTokenIfPossible) throws CacheException {
        return createNewTokenOrReuseExistingToken(value, reuseTokenIfPossible, true);
    }

    @Override
    @Transactional
    @SuppressWarnings({"java:S6809", "PMD.AvoidUncheckedExceptionsInSignatures"})
    public Optional<Token> store(ServiceContext context) throws CacheException {
        return store(context, false);
    }

    @Override
    @Transactional
    @SuppressWarnings({"java:S6809", "PMD.AvoidUncheckedExceptionsInSignatures"})
    public Optional<Token> store(ServiceContext context, Boolean reuseTokenIfPossible) throws CacheException {
        log.trace("storing new token");
        String sharedId = getIdForServiceContext(context);
        if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            Object sharedToken = redisTemplate.opsForValue().get(sharedId);
            Token token = Token.readToken((String) sharedToken, properties.isMapTokenToSession(),
                                          getTokenValidator()
            );
            Optional<CacheValue> value = getRefreshValueWithResetExpiration(token);
            if (value.isPresent()) {
                // store into redis without validation of the associated token
                Optional<Token> createdToken = Optional.of(createNewTokenOrReuseExistingToken(value.get(), reuseTokenIfPossible, true).get());
                log.trace("stored new token");
                return createdToken;
            } else {
                log.debug("Not looking up shared token either turned off or not  forced");
                Optional<Token> createdToken =  Optional.of(store(createValue(context), reuseTokenIfPossible).get());
                log.trace("stored new token");
                return createdToken;
            }
        } else {
            log.debug("Not looking up shared token either turned off or not forced");
            Optional<Token> createdToken = Optional.of(store(createValue(context), reuseTokenIfPossible).get());
            log.trace("stored new token");
            return createdToken;
        }
    }

    private boolean reuseTokenBasedOnCredentials(Boolean reuseTokenIfPossible) {
        return properties.isShareTokenForSameCredentials()
            || BooleanUtils.isTrue(reuseTokenIfPossible);
    }


    @Override
    @Transactional
    @SuppressWarnings({"java:S6809", "PMD.AvoidUncheckedExceptionsInSignatures"})
    public Optional<CacheValue> lookup(Token tokenToLookup) throws CacheException {
        log.trace("Looking up the token");

        Optional<CacheValue> val = read(tokenToLookup);

        boolean tokenIsValid = tokenToLookup.validate();
        if (val.isPresent() && !tokenIsValid) {
            redisTemplate.delete(tokenToLookup.getKey());
            val = Optional.empty();
            log.debug("Token manually removed as it is no longer valid");
        } else {
            if (val.isPresent() && val.get() instanceof DownstreamTokenisableValue downstreamToken && downstreamToken.doesRequireRefresh()) {
                log.debug("Token cache value needs refreshing");
                downstreamToken.performRefresh();
                log.debug("Token cache value has been refreshed");
            }

            if (val.isPresent()) {
                log.debug("Resetting token expiration");
                redisTemplate.opsForValue().set(tokenToLookup.getKey(), val.get(), secondsToExpire());
                redisTemplate.expire(tokenToLookup.getKey(), secondsToExpire());
                log.debug("Reset token expiration");
            }
        }

        log.trace("Returning looked up token");
        return val;
    }

    protected abstract CacheValue getValue(CacheValue holder);

    protected abstract TokenValidator getTokenValidator();

    @Override
    public Token getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession(), getTokenValidator());
    }

    @Override
    @Transactional
    public void evict(Token holder) {
        log.debug("Evicting the token");

        if (properties.isShareTokenForSameCredentials()) {
            log.debug("Token was not evicted as it can be shared. The token expiration timeout is still applicable");
        } else {
            redisTemplate.delete(holder.getKey());
            log.debug("Evicted the token");
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
            return Optional.of(Token.readToken((String) value, properties.isMapTokenToSession(), getTokenValidator()));
        }

        return Optional.empty();
    }

    private TokenWithStatus getTokenWithStatusFromCache(CacheValue valueToBeCached, boolean shouldValidateToken) {
        log.debug("Looking up token in cache");
        TokenWithStatus tokenWithStatus = new TokenWithStatus();

        Optional<Token> tokenFromCacheOpt = read(valueToBeCached);
        if (tokenFromCacheOpt.isEmpty()) {
            log.debug("Token not found in cache");
            tokenWithStatus.setStatus(TokenStatus.NOT_FOUND);
            return tokenWithStatus;
        }

        log.debug("Found the token in the cache");
        Token tokenFromCache = tokenFromCacheOpt.get();
        tokenWithStatus.setToken(tokenFromCache);
        if (shouldValidateToken) {
            boolean tokenIsValid = tokenFromCache.validate(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY);
            if (tokenIsValid) {
                tokenWithStatus.setStatus(TokenStatus.OK);
            } else {
                tokenWithStatus.setStatus(TokenStatus.INVALID);
            }
        } else {
            tokenWithStatus.setStatus(TokenStatus.OK);
        }

        return tokenWithStatus;
    }

    /**
     * This function is key for storing to redis. It takes a service context and if reuse of token is required looks
     * for the redis token and validates it (depending on the token representation). If the token is not valid then we acquire
     * a new token and store in redis. If we do not require reuse a new token is generated each time.
     * @param cachedValueIncludingDartsApiToken The value to cache.
     * @param reuseTokenIfPossible Do we want to use a shared token if we can.
     * @param validateToken Do we wish to validate the token if one exists and sharing is required.
     * @return The token to use. This maybe a shared token or a branch new one.
     */
    @SuppressWarnings({"java:S6809", "PMD.AvoidUncheckedExceptionsInSignatures"})
    private Optional<Token> createNewTokenOrReuseExistingToken(CacheValue cachedValueIncludingDartsApiToken,
                                                               Boolean reuseTokenIfPossible, boolean validateToken) throws CacheException {

        log.trace("Storing the supplied value");
        Token consumerToken;

        if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            TokenWithStatus tokenWithStatusFromCache = getTokenWithStatusFromCache(cachedValueIncludingDartsApiToken, validateToken);

            if (tokenWithStatusFromCache.getStatus() == TokenStatus.OK) {
                consumerToken = tokenWithStatusFromCache.getToken();
            } else {
                consumerToken = createConsumerTokenWithSharedRedisLock(cachedValueIncludingDartsApiToken);
            }
        } else {
            consumerToken = createToken(cachedValueIncludingDartsApiToken.getServiceContext());
        }

        storeToRedis(consumerToken, cachedValueIncludingDartsApiToken);
        log.trace("Token value stored in cache");
        return Optional.of(consumerToken);
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
        redisTemplate.expire(consumerToken.getKey(), secondsToExpire);
        redisTemplate.expire(cachedValueIncludingdartsApiToken.getSharedKey(), secondsToExpire);
    }

    private Token createConsumerTokenWithSharedRedisLock(CacheValue value) {
        log.trace("Locking the shared token");
        // ensure that one token is acquired according to the shared token id. This prevents us from overwhelming
        // the underlying idp
        CacheLockableUnitOfWork distributedLockWork = new CacheLockableUnitOfWork(lockRegistry);
        return distributedLockWork.execute(() -> {

            // check the token in the cache again to ensure it hasn't been updated by another thread
            TokenWithStatus tokenWithStatusFromCache = getTokenWithStatusFromCache(value, true);

            if (tokenWithStatusFromCache.getStatus() == TokenStatus.OK) {
                //must have been updated by another thread, so sending back valid token.
                return tokenWithStatusFromCache.getToken();
            }

            if (tokenWithStatusFromCache.getStatus() == TokenStatus.INVALID) {
                log.debug("Cached token is invalid. Deleting from Cache.");
                redisTemplate.delete(value.getSharedKey());
                redisTemplate.delete(tokenWithStatusFromCache.getToken().getKey());
            }

            Token token =  createToken(value.getServiceContext());

            log.trace("Locking finished");
            return token;
        }, value.getSharedKey());
    }

    protected abstract Token createToken(ServiceContext context);

    protected abstract String getIdForServiceContext(ServiceContext context);

}

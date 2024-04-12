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


    private Duration getSecondsToExpire() {
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
        return storeIntoRedis(value, reuseTokenIfPossible, true);
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
                return Optional.of(storeIntoRedis(value.get(), reuseTokenIfPossible, false).get());
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
                redisTemplate.opsForValue().set(tokenToLookup.getId(), val.get(), getSecondsToExpire());
                redisTemplate.expire(tokenToLookup.getId(), getSecondsToExpire());
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

            redisTemplate.delete(holder.getId());

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
        Object value = redisTemplate.opsForValue().get(token.getId());

        if (value != null) {
            return Optional.of(getValue((CacheValue) value));
        }

        return Optional.empty();
    }

    private Optional<Token> getTokenValueWithResetExpiration(CacheValue holder) {
        Object value = redisTemplate.opsForValue().get(holder.getId());

        if (value != null) {
            return Optional.of(Token.readToken((String) value, properties.isMapTokenToSession(), getValidateToken()));
        }

        return Optional.empty();
    }


    @SuppressWarnings("java:S6809")
    private Optional<Token> storeIntoRedis(CacheValue value, Boolean reuseTokenIfPossible, boolean validateToken) throws CacheException {

        log.debug("Storing the supplied value");

        Optional<Token> tokenToUse = Optional.empty();

        // if we have found a token, but it is invalid then invalidate it and force a new token to be created
        if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            log.debug("Looking up token in cache");

            tokenToUse = read(value);

            if (tokenToUse.isPresent() && validateToken) {
                log.debug("Found the token in the cache");
                if (!tokenToUse.get().validate(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY)) {
                    tokenToUse = Optional.empty();
                }
            } else {
                log.debug("Token not found in cache");
            }

            if (tokenToUse.isEmpty()) {
                // ensure that one token is acquired when we know one is needed
                CacheLockableUnitOfWork distributedLockWork = new CacheLockableUnitOfWork(lockRegistry);
                tokenToUse = distributedLockWork.execute(() -> {

                    // read the token again one at a time to skip token creation
                    Optional<Token> foundToken = read(value);
                    if (foundToken.isPresent() && !foundToken.get().validate(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY)) {
                        foundToken = Optional.empty();
                    }

                    // if the token is invalid then create the token
                    if (!foundToken.isPresent()) {
                        return createToken(value.getServiceContext());
                    }
                    return foundToken.get();
                }, value.getId());
            }
        } else {
            tokenToUse = Optional.of(createToken(value.getServiceContext()));
        }

        // now update the redis ids with the shared token details as well as the token for the request
        Duration secondsToExpire = getSecondsToExpire();
        redisTemplate.opsForValue().set(tokenToUse.get().getId(), value, secondsToExpire);
        redisTemplate.opsForValue().set(value.getId(), tokenToUse.get().getTokenString(), secondsToExpire);
        redisTemplate.expire(tokenToUse.get().getId(), getSecondsToExpire());
        redisTemplate.expire(value.getId(), getSecondsToExpire());

        log.debug("Token value stored in cache");

        return tokenToUse;
    }

    protected abstract Token createToken(ServiceContext context) throws CacheException;

    protected abstract String getIdForServiceContext(ServiceContext context) throws CacheException;

}

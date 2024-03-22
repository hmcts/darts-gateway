package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 *
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

        log.debug("Storing the supplied value");

        CacheLockableUnitOfWork distributedLockWork = new CacheLockableUnitOfWork(lockRegistry);
        Optional<Token> token = distributedLockWork.execute(() -> {
            Optional<Token> tokenToUse = Optional.empty();

            // if we have found a token but it is invalid then invalid it and force a new token to be created
            if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
                log.debug("Looking up token for the same credentials");

                tokenToUse = lookup(value);

                if (tokenToUse.isPresent() && !tokenToUse.get().valid(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY)) {
                    tokenToUse = Optional.empty();
                }

                log.debug("Found the token for the credentials");
            }

            if (tokenToUse.isEmpty()) {
                tokenToUse = Optional.of(createToken(value.getServiceContext()));

                log.debug("Creating a new token");

                CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

                work.execute(t -> {
                    redisTemplate.opsForValue().set(t.getId(), value, getSecondsToExpire());

                    redisTemplate.opsForValue().set(value.getId(), t.getTokenString(false).orElse(""), getSecondsToExpire());

                }, tokenToUse.get());
            }

            return tokenToUse;
        }, reuseTokenBasedOnCredentials(reuseTokenIfPossible), value.getId());

        log.debug("Token value stored in cache");

        if (token.isPresent() && reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

            // if we have a global token stored then make sure the token has the service context if
            // not add one
            work.execute(t -> {
                Optional<CacheValue> concurrentValue = lookup(t);
                if (concurrentValue.isEmpty()) {
                    redisTemplate.opsForValue()
                            .set(t.getId(), value, getSecondsToExpire());
                } else {
                    redisTemplate.expire(t.getId(), getSecondsToExpire());
                }
            }, token.get());
        }

        return token;
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
        Optional<Token> foundToken;

        if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            log.debug("Looking for the shared token configuration");

            CacheLockableUnitOfWork distributedLockWork = new CacheLockableUnitOfWork(lockRegistry);
            String sharedId = getIdForServiceContext(context);
            foundToken = distributedLockWork.execute(() -> {
                Optional<Token> foundTokenForSharedId = Optional.empty();
                Object sharedToken = redisTemplate.opsForValue().get(sharedId);

                if (sharedToken != null) {
                    redisTemplate.expire(sharedId, getSecondsToExpire());

                    CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

                    Token token = Token.readToken((String) sharedToken, properties.isMapTokenToSession(),
                                                  getValidateToken()
                    );

                    boolean applicable = work.executeIsApplicable(t -> touchToken(t).isPresent(), token);

                    if (applicable) {
                        foundTokenForSharedId = Optional.of(token);

                        log.debug("Found the token for the shared credentials");
                    } else {
                        foundTokenForSharedId = store(createValue(context), reuseTokenIfPossible);
                        log.debug("Not found the token for the shared credentials");
                    }
                } else {
                    foundTokenForSharedId = store(createValue(context), reuseTokenIfPossible);
                }

                return foundTokenForSharedId;
            }, sharedId);
        } else {
            log.debug("Not looking up shared token either turned off or not forced");
            foundToken = store(createValue(context), reuseTokenIfPossible);
        }

        return foundToken;
    }

    private Optional<CacheValue> touchToken(Token token) {
        Optional<CacheValue> tokenValue = lookup(token);

        if (!token.valid(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY)) {
            tokenValue = Optional.empty();
        }

        if (tokenValue.isPresent()) {
            redisTemplate.expire(token.getId(), getSecondsToExpire());
        }

        return tokenValue;
    }

    private boolean reuseTokenBasedOnCredentials(Boolean reuseTokenIfPossible) {
        return properties.isShareTokenForSameCredentials()
                || (reuseTokenIfPossible != null && reuseTokenIfPossible);
    }

    private Optional<Token> lookup(CacheValue context) throws CacheException {
        return read(context);
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S6809")
    public Optional<CacheValue> lookup(Token holder) throws CacheException {

        log.debug("Looking up the token");

        // if the token is not valid then
        CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

        return work.executeForValueReturn(t -> {
            Optional<CacheValue> val = getValue(holder);

            boolean tokenValid = holder.valid();
            if (val.isPresent() && !tokenValid) {
                evict(holder);
                val = Optional.empty();
                log.debug("Token manually removed as it is no longer valid");
            } else {
                if (val.isPresent() && val.get() instanceof DownstreamTokenisableValue downstreamToken && downstreamToken.doesRequireRefresh()) {
                    log.debug("Token cache value needs refreshing");
                    downstreamToken.performRefresh();
                    log.debug("Token cache value has been refreshed");

                    redisTemplate.opsForValue().set(t.getId(), val.get(), getSecondsToExpire());
                }
            }

            log.debug("Returning found value");
            return val;
        }, holder);
    }

    protected Optional<CacheValue> getValue(Token holder) throws CacheException {
        CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

        return work.executeForValueReturn(this::read, holder);
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
        CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

        work.execute(t -> {
            log.debug("Evicting the token");

            if (!properties.isShareTokenForSameCredentials()) {

                redisTemplate.delete(t.getId());

                log.debug("Evicted the token");
            } else {
                log.debug("Token was not evicted as it can be shared. The token expiration timeout is still applicable");
            }
        }, holder);
    }

    private Optional<CacheValue> read(Token holder) {
        return getRefreshValueWithResetExpiration(holder);
    }

    private Optional<Token> read(CacheValue holder) {
        return getTokenValueWithResetExpiration(holder);
    }

    private Optional<CacheValue> getRefreshValueWithResetExpiration(Token holder) {
        Object value = redisTemplate.opsForValue().get(holder.getId());
        redisTemplate.expire(holder.getId(), getSecondsToExpire());

        if (value != null) {
            return Optional.of(getValue((CacheValue) value));
        }

        return Optional.empty();
    }

    private Optional<Token> getTokenValueWithResetExpiration(CacheValue holder) {
        Object value = redisTemplate.opsForValue().get(holder.getId());
        redisTemplate.expire(holder.getId(), getSecondsToExpire());

        if (value != null) {
            return Optional.of(Token.readToken((String) value, properties.isMapTokenToSession(), getValidateToken()));
        }

        return Optional.empty();
    }

    protected abstract Token createToken(ServiceContext context) throws CacheException;

    protected abstract String getIdForServiceContext(ServiceContext context) throws CacheException;

}

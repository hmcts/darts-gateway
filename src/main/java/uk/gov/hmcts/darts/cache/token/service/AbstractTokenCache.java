package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractTokenCache implements TokenRegisterable {

    private final RedisTemplate<String, Object> redisTemplate;

    private final LockRegistry lockRegistry;

    protected final CacheProperties properties;


    private Duration getSecondsToExpire() {
        return Duration.of(properties.getEntryTimeToIdleSeconds(), ChronoUnit.SECONDS);
    }

    @Override
    @Transactional
    public Optional<Token> store(RefreshableCacheValue value) throws CacheException {
        return store(value, null);
    }

    @Override
    @Transactional
    public Optional<Token> store(RefreshableCacheValue value, Boolean reuseTokenIfPossible) throws CacheException {

        log.info("Storing the supplied value");

        CacheLockableUnitOfWork distributedLockWork = new CacheLockableUnitOfWork(lockRegistry);
        Optional<Token> token = distributedLockWork.execute(() -> {
            Optional<Token> tokenToUse = Optional.empty();

            // if we have found a token but it is invalid then invalid it and force a new token to be created
            if (reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
                log.info("Looking up token for the same credentials");

                tokenToUse = lookup(value);

                if (tokenToUse.isPresent() && !tokenToUse.get().valid()) {
                    tokenToUse = Optional.empty();
                }

                log.info("Found the token for the credentials");
            }

            if (tokenToUse.isEmpty()) {
                tokenToUse = createToken(value.getServiceContext());

                log.info("Creating a new token");

                if (tokenToUse.isPresent()) {
                    CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

                    work.execute((t) -> {
                        redisTemplate.opsForValue().set(t.getId(), value, getSecondsToExpire());

                        redisTemplate.opsForValue().set(value.getId(), t.getToken().orElse(""), getSecondsToExpire());

                    }, tokenToUse.get());
                }
            }

            return tokenToUse;
        }, reuseTokenBasedOnCredentials(reuseTokenIfPossible));

        log.info("Token value stored in cache");

        if (token.isPresent() && reuseTokenBasedOnCredentials(reuseTokenIfPossible)) {
            CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

            // if we have a global token stored then make sure the token has the service context if
            // not add one
            work.execute((t) -> {
                Optional<RefreshableCacheValue> concurrentValue = lookup(t);
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

    private boolean reuseTokenBasedOnCredentials(Boolean reuseTokenIfPossible) {
        return properties.isShareTokenForSameCredentials()
                || (reuseTokenIfPossible != null && reuseTokenIfPossible);
    }

    protected abstract Predicate<String> getValidateToken();

    @Override
    public Token getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession(), getValidateToken());
    }

    @Override
    @Transactional
    public Optional<Token> lookup(RefreshableCacheValue context) throws CacheException {
        return read(context);
    }

    @Override
    @Transactional
    public Optional<RefreshableCacheValue> lookup(Token holder) throws CacheException {

        log.info("Looking up the token");

        // if the token is not valid then
        CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

        return work.executeForRefreshValueReturn((t) -> {
            Optional<RefreshableCacheValue> val = getValue(holder);

            if (val.isPresent() && !holder.valid()) {
                evict(holder);
                val = Optional.empty();
                log.info("Token manually removed as it is no longer valid");
            } else {
                if (val.isPresent()) {
                    if (val.get().refresh()) {
                        log.info("Token cache value needs refreshing");
                        val.get().performRefresh();
                        log.info("Token cache value has been refreshed");

                        redisTemplate.opsForValue().set(t.getId(), val.get(), getSecondsToExpire());
                    }
                }
            }

            log.info("Returning found value {}", holder.getId());
            return val;
        }, holder);
    }

    protected Optional<RefreshableCacheValue> getValue(Token holder) throws CacheException {
        CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

        return work.executeForRefreshValueReturn(this::read, holder);
    }

    protected abstract RefreshableCacheValue getValue(RefreshableCacheValue holder) throws CacheException;

    @Override
    @Transactional
    public void evict(Token holder) throws CacheException {
        CacheLockableUnitOfWork work = new CacheLockableUnitOfWork(lockRegistry);

        work.execute((t) -> {
            log.info("Evicting the token");

            if (!properties.isShareTokenForSameCredentials()) {

                redisTemplate.delete(t.getId());

                log.info("Evicted the token");
            } else {
                log.info("Token was not evicted as it can be shared. The token expiration timeout is still applicable");
            }
        }, holder);
    }

    private Optional<RefreshableCacheValue> read(Token holder) {
        return getRefreshValueWithResetExpiration(holder);
    }

    private Optional<Token> read(RefreshableCacheValue holder) {
        return getTokenValueWithResetExpiration(holder);
    }

    private Optional<RefreshableCacheValue> getRefreshValueWithResetExpiration(Token holder) {
        Object value = redisTemplate.opsForValue().get(holder.getId());
        redisTemplate.expire(holder.getId(), getSecondsToExpire());

        if (value != null) {
            return Optional.of(getValue((RefreshableCacheValue) value));
        }

        return Optional.empty();
    }

    private Optional<Token> getTokenValueWithResetExpiration(RefreshableCacheValue holder) {
        Object value = redisTemplate.opsForValue().get(holder.getId());
        redisTemplate.expire(holder.getId(), getSecondsToExpire());

        if (value != null) {
            return Optional.of(Token.readToken((String) value, properties.isMapTokenToSession(), getValidateToken()));
        }

        return Optional.empty();
    }

    protected abstract Optional<Token> createToken(ServiceContext context) throws CacheException;
}
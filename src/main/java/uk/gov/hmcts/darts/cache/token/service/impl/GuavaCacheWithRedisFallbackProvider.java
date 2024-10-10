package uk.gov.hmcts.darts.cache.token.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.service.CacheProvider;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

/**
 * A provider that prioritises a local cache and then delegates to redis if the values are not found.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GuavaCacheWithRedisFallbackProvider implements CacheProvider {

    private final RedisTemplate<String, Object> redisTemplate;

    private Cache<Object, Object> cache;

    @Value("$darts-gateway.cache.entry-time-to-idle-seconds")
    private int entryTimeToIdleSeconds;

    @PostConstruct
    public void postConstruct() {
        redisTemplate.setEnableTransactionSupport(true);

        // set the local cache time to live value
        cache = CacheBuilder.newBuilder()
            .expireAfterAccess(entryTimeToIdleSeconds, TimeUnit.SECONDS)
            .build();
    }


    @Override
    public String getStringFromString(String key) {
        Object value = null;
        try {
            value = cache.get(key, () -> redisTemplate.opsForValue().get(key));
        } catch (ExecutionException executionException) {
            log.error("Error getting value from cache", executionException);
        }

        if (value != null) {
            return (String) value;
        }

        return null;
    }

    @Override
    public CacheValue getCacheValue(String key) {
        Object value = null;
        try {
            value = cache.get(key, () -> redisTemplate.opsForValue().get(key));
        } catch (ExecutionException executionException) {
            log.error("Error getting value from cache", executionException);
        }

        if (value != null) {
            return (CacheValue) value;
        }
        return null;
    }

    @Override
    public boolean delete(String key) {
        cache.invalidate(key);
        return redisTemplate.delete(key);
    }

    @Override
    public void setKeyValue(String key, String value) {
        cache.put(key, value);
        redisTemplate.opsForValue().set(key, value, secondsToExpire());
    }

    @Override
    public void setKeyValue(String key, CacheValue value) {
        cache.put(key, value);
        redisTemplate.opsForValue().set(key, value, secondsToExpire());
    }

    @Override
    public void setExpire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    private Duration secondsToExpire() {
        return Duration.of(entryTimeToIdleSeconds, ChronoUnit.SECONDS);
    }
}
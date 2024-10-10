package uk.gov.hmcts.darts.cache.token.service.impl;

import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.core.RedisTemplate;
import uk.gov.hmcts.darts.cache.token.service.CacheProvider;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

/**
 * A provider that prioritises a local cache and then delegates to redis if the values are not found.
 */
@Slf4j
public class GuavaCacheWithRedisFallbackProvider implements CacheProvider {

    private final RedisTemplate<String, Object> redisTemplate;

    private ConcurrentMapCache cache;

    @Value("${darts-gateway.cache.entry-time-to-idle-seconds}")
    private int entryTimeToIdleSeconds;

    public static final String TOKEN_CACHE = "token_cache";

    public GuavaCacheWithRedisFallbackProvider(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void postConstruct() {
        redisTemplate.setEnableTransactionSupport(true);

        // set the local cache time to live value
        cache = new ConcurrentMapCache(TOKEN_CACHE, CacheBuilder.newBuilder()
            .expireAfterWrite(entryTimeToIdleSeconds, TimeUnit.SECONDS)
            .build().asMap(), false);
    }

    @Override
    public String getStringFromString(String key) {
        Cache.ValueWrapper  value = null;
        value = cache.get(key);
        String returnString = null;

        if (value != null) {
            returnString = (String) value.get();
        } else {
            returnString = (String) redisTemplate.opsForValue().get(key);
        }

        return returnString;
    }

    @Override
    public CacheValue getCacheValue(String key) {
        Cache.ValueWrapper  value = null;
        value = cache.get(key);
        CacheValue returnString = null;

        if (value != null) {
            returnString = (CacheValue) value.get();
        } else {
            returnString = (CacheValue) redisTemplate.opsForValue().get(key);
        }

        return returnString;
    }

    @Override
    public boolean delete(String key) {
        cache.evict(key);
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

    @Override
    public void cleanAll() {
        cache.invalidate();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
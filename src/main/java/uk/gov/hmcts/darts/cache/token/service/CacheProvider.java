package uk.gov.hmcts.darts.cache.token.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.time.Duration;

@Component
public interface CacheProvider {
    /**
     * gets a string for a key
     * @param key The key
     * @return The string
     */
    String getStringFromString(String string);

    /**
     * gets the cache value key
     * @return The cache value
     */
    CacheValue getCacheValue(String key);

    /**
     * deletes a key from the cache
     */
    boolean delete(String string);

    /**
     * sets the key and associated expiry
     * @param key The key to use
     * @param value The string value
     */
    void setKeyValue(String key, String value);

    /**
     * sets the key and associated expiry
     * @param key The key to use
     * @param value The cache value
     */
    void setKeyValue(String key, CacheValue value);

    /**
     * set the expiry for a key
     */
    void setExpire(String key, Duration duration);
}
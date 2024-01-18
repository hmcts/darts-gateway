package uk.gov.hmcts.darts.cache.token;

import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InMemoryValueOperations implements ValueOperations<String, Object> {

    private final Map<String, Object> memoryBackedDataStore = new HashMap<>();

    @Override
    public void set(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(String key, Object value, long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(String key, Object value, Duration timeout) {
        memoryBackedDataStore.put(key, value);
    }

    @Override
    public Boolean setIfAbsent(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, Duration timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean setIfPresent(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean setIfPresent(String key, Object value, Duration timeout) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Boolean setIfPresent(String key, Object value, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void multiSet(Map<? extends String, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends String, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAndDelete(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAndExpire(String key, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAndExpire(String key, Duration timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAndPersist(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAndSet(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> multiGet(Collection<String> keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long increment(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long increment(String key, long delta) {
        return null;
    }

    @Override
    public Double increment(String key, double delta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long decrement(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long decrement(String key, long delta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer append(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get(String key, long start, long end) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object key) {
        return memoryBackedDataStore.get(key);
    }

    @Override
    public Long size(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean getBit(String key, long offset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Long> bitField(String key, BitFieldSubCommands subCommands) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RedisOperations<String, Object> getOperations() {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> getModel() {
        return memoryBackedDataStore;
    }
}

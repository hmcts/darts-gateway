package uk.gov.hmcts.darts.ctxtregistry;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;

@RequiredArgsConstructor
public abstract class AbstractTokenCache implements TokenRegisterable {

    protected final Cache cacheManager;

    @Override
    public void store(TokenHolder holder, RefreshableCacheValue value) {
        cacheManager.put(holder, value);
    }

    @Override
    public RefreshableCacheValue lookup(TokenHolder holder) {
        RefreshableCacheValue val = getValue(holder);
        if (val != null) {
            val.refresh();
        }

        return val;
    }

    public RefreshableCacheValue lookupNoRefresh(TokenHolder holder) {
        return getValue(holder);
    }

    protected abstract RefreshableCacheValue getValue(TokenHolder holder);

    @Override
    public void evict(TokenHolder holder) {
        cacheManager.evict(holder);
    }
}

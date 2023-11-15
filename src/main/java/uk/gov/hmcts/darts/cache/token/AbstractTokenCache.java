package uk.gov.hmcts.darts.cache.token;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

@RequiredArgsConstructor
public abstract class AbstractTokenCache implements TokenRegisterable {

    protected final Cache cacheManager;

    protected final ContextRegistryProperties properties;

    @Override
    public void store(Token holder, RefreshableCacheValue value) {
        cacheManager.put(holder, value);
    }

    @Override
    public RefreshableCacheValue lookup(Token holder, boolean refresh) {
        RefreshableCacheValue val = getValue(holder);
        if (val != null && refresh) {
            val.refresh();
        }

        return val;
    }
    protected abstract RefreshableCacheValue getValue(Token holder);

    @Override
    public void evict(Token holder) {
        cacheManager.evict(holder);
    }
}

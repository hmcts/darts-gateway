package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;

public class DefaultContextRegistryCache extends AbstractTokenCache {

    public DefaultContextRegistryCache(Cache cacheManager) {
        super(cacheManager);
    }

    @Override
    protected ServiceContextCacheValue getValue(TokenHolder holder) {
        return cacheManager.get(holder, ServiceContextCacheValue.class);
    }

    @Override
    public TokenHolder createToken(ServiceContext context) {
        return  TokenHolder.generateToken();
    }

    @Override
    public ServiceContextCacheValue createValue(ServiceContext context) {
        return new ServiceContextCacheValue(context);
    }

}

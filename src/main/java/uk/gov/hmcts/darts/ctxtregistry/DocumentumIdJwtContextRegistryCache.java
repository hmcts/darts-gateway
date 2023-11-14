package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;


//TODO: To be implemented in a later ticket
public class DocumentumIdJwtContextRegistryCache extends AbstractTokenCache {
    public DocumentumIdJwtContextRegistryCache(Cache cacheManager) {
        super(cacheManager);
    }

    @Override
    protected CacheValueWithJwt getValue(TokenHolder holder) {
        return cacheManager.get(holder, CacheValueWithJwt.class);
    }

    @Override
    public TokenHolder createToken(ServiceContext context) {
        return TokenHolder.generateToken();
    }

    @Override
    public CacheValueWithJwt createValue(ServiceContext context) {
        return null;
    }
}

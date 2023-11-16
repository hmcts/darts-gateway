package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

import java.util.Optional;

public class DefaultCache extends AbstractTokenCache {

    public DefaultCache(Cache cacheManager, ContextRegistryProperties properties) {
        super(cacheManager, properties);
    }

    @Override
    protected ServiceContextCacheValue getValue(Token holder) {
        return cacheManager.get(holder, ServiceContextCacheValue.class);
    }

    @Override
    public Optional<Token> createToken(ServiceContext context) {
        return Optional.of(Token.generateDocumentumToken(properties.isMapTokenToSession()));
    }

    @Override
    public Optional<Token> getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession());
    }

    @Override
    public ServiceContextCacheValue createValue(ServiceContext context) {
        return new ServiceContextCacheValue(context);
    }
}

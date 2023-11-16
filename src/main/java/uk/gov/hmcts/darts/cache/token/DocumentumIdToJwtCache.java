package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

import java.util.Optional;


//TODO: To be implemented in a later ticket if the jwt cant successfully be returned
public class DocumentumIdToJwtCache extends AbstractTokenCache {
    public DocumentumIdToJwtCache(Cache cacheManager, ContextRegistryProperties properties) {
        super(cacheManager, properties);
    }

    @Override
    protected CacheValueWithJwt getValue(Token holder) {
        return cacheManager.get(holder, CacheValueWithJwt.class);
    }

    @Override
    public Optional<Token> createToken(ServiceContext context) {
        return Optional.of(Token.generateDocumentumToken(properties.isMapTokenToSession()));
    }

    @Override
    public CacheValueWithJwt createValue(ServiceContext context) {
        return null;
    }

    @Override
    public Optional<Token> getToken(String token) {
        return Optional.empty();
    }
}

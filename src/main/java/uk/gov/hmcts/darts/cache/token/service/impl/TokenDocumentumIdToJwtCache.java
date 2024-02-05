package uk.gov.hmcts.darts.cache.token.service.impl;

import documentum.contextreg.ServiceContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue;

/**
 * A documentum token cache that maps to {@link uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue} which itself
 * stores and manages a downstream jwt token.
 */
public class TokenDocumentumIdToJwtCache extends AbstractTokenCache {
    public final TokenValidator validator = (expiryBefore, token) -> true;

    private final TokenGeneratable cache;

    public TokenDocumentumIdToJwtCache(RedisTemplate<String, Object> template, TokenGeneratable cache, CacheProperties properties,
                                       LockRegistry registry) {
        super(template, registry, properties);
        this.cache = cache;
    }

    @Override
    protected TokenValidator getValidateToken() {
        return validator;
    }

    @Override
    protected Token createToken(ServiceContext context) {
        return Token.generateDocumentumToken(properties.isMapTokenToSession(), getValidateToken());
    }

    @Override
    public RefeshableTokenCacheValue createValue(ServiceContext context) throws CacheException {
        return new RefeshableTokenCacheValue(context, cache);
    }

    @Override
    protected CacheValue getValue(CacheValue holder) throws CacheException {
        return new RefeshableTokenCacheValue((RefeshableTokenCacheValue) holder, cache);
    }

    @Override
    public Token getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession(), validator);
    }

    @Override
    public String getIdForServiceContext(ServiceContext serviceContext) throws CacheException {
        return RefeshableTokenCacheValue.getId(serviceContext);
    }
}

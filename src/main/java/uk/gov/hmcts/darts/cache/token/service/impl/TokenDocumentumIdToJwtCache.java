package uk.gov.hmcts.darts.cache.token.service.impl;

import documentum.contextreg.ServiceContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.ServiceContextCacheValue;

/**
 * A documentum token cache that maps to {@link uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue} which itself
 * stores and manages a downstream jwt token.
 */
public class TokenDocumentumIdToJwtCache extends AbstractTokenCache {
    public static final TokenValidator VALIDATOR = (expiryBefore, token) -> true;

    private final TokenGeneratable cache;

    public TokenDocumentumIdToJwtCache(RedisTemplate<String, Object> template, TokenGeneratable cache, CacheProperties properties,
                                       LockRegistry registry) {
        super(template, registry, properties);
        this.cache = cache;
    }

    @Override
    protected TokenValidator getTokenValidator() {
        return VALIDATOR;
    }

    @Override
    protected Token createToken(ServiceContext context) {
        return Token.generateDocumentumToken(properties.isMapTokenToSession(), getTokenValidator());
    }

    @Override
    public RefeshableTokenCacheValue createValue(ServiceContext context) {
        return new RefeshableTokenCacheValue(context, cache);
    }

    @Override
    protected CacheValue getValue(CacheValue holder) {
        return new RefeshableTokenCacheValue((RefeshableTokenCacheValue) holder, cache);
    }

    @Override
    public Token getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession(), VALIDATOR);
    }

    @Override
    public String getIdForServiceContext(ServiceContext serviceContext) {
        return ServiceContextCacheValue.getId(serviceContext);
    }
}

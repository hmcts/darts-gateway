package uk.gov.hmcts.darts.cache.token.service.impl;

import documentum.contextreg.ServiceContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue;

import java.util.function.Predicate;

public class TokenDocumentumIdToJwtCache extends AbstractTokenCache {
    public static final Predicate<String> TOKEN_VALIDATION = token -> true;

    private final TokenGeneratable cache;

    public TokenDocumentumIdToJwtCache(RedisTemplate<String, Object> template, TokenGeneratable cache, CacheProperties properties,
                                       LockRegistry registry) {
        super(template, registry, properties);
        this.cache = cache;
    }

    @Override
    protected Predicate<String> getValidateToken() {
        return TOKEN_VALIDATION;
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
        return Token.readToken(token, properties.isMapTokenToSession(), TOKEN_VALIDATION);
    }
}

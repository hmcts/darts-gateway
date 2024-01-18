package uk.gov.hmcts.darts.cache.token.documentum;

import documentum.contextreg.ServiceContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.RefreshableCacheValue;
import uk.gov.hmcts.darts.cache.token.RefreshableCacheValueWithJwt;
import uk.gov.hmcts.darts.cache.token.Token;
import uk.gov.hmcts.darts.cache.token.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.util.Optional;
import java.util.function.Predicate;

public class DocumentumIdToJwtCache extends AbstractTokenCache {
    public static Predicate<Token> TOKEN_VALIDATION = token -> {
        return true;
    };

    private final TokenGeneratable cache;

    public DocumentumIdToJwtCache(RedisTemplate<String, Object> template, TokenGeneratable cache, CacheProperties properties,
                                  LockRegistry registry) {
        super(template, registry, properties);
        this.cache = cache;
    }

    @Override
    protected Predicate<Token> getValidateToken() {
        return TOKEN_VALIDATION;
    }

    @Override
    protected Optional<Token> createToken(ServiceContext context) {
        return Optional.of(Token.generateDocumentumToken(properties.isMapTokenToSession(), getValidateToken()));
    }

    @Override
    public RefreshableCacheValueWithJwt createValue(ServiceContext context) throws CacheException {
        return new RefreshableCacheValueWithJwt(context, cache);
    }

    @Override
    protected RefreshableCacheValue getValue(RefreshableCacheValue holder) throws CacheException {
        return new RefreshableCacheValueWithJwt((RefreshableCacheValueWithJwt) holder, cache);
    }

    @Override
    public Token getToken(String token) {
        return Token.readToken(token, properties.isMapTokenToSession(), TOKEN_VALIDATION);
    }
}

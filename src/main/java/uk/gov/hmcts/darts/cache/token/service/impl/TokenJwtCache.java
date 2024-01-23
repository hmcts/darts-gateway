package uk.gov.hmcts.darts.cache.token.service.impl;

import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.RefreshableCacheValue;
import uk.gov.hmcts.darts.cache.token.service.ServiceContextCacheValue;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class TokenJwtCache extends AbstractTokenCache implements TokenGeneratable {
    public Predicate<String> validationPredicate;

    @Override
    protected Predicate<String> getValidateToken() {
        if (validationPredicate == null) {
            validationPredicate =  (token) -> validator.validate(token);
        }

        return validationPredicate;
    }

    private final TokenGenerator generator;

    private final TokenValidator validator;

    public TokenJwtCache(RedisTemplate<String, Object> template, TokenGenerator generator,
                         CacheProperties ctxtregproperties, LockRegistry registry, TokenValidator validator) {
        super(template, registry, ctxtregproperties);
        this.generator = generator;
        this.validator = validator;
    }

    @Override
    public RefreshableCacheValue createValue(ServiceContext serviceContext) throws CacheException {
        return new ServiceContextCacheValue(serviceContext);
    }

    @Override
    protected RefreshableCacheValue getValue(RefreshableCacheValue holder) throws CacheException {
        return holder;
    }

    @Override
    public Optional<Token> createToken(ServiceContext context) throws CacheException {
        try {
            List<Identity> identities = context.getIdentities();
            if (identities.isEmpty()) {
                return Optional.empty();
            }

            Identity identity = identities.get(0);
            if (identity instanceof BasicIdentity basicIdentity) {
                String jwtToken = generator.acquireNewToken(
                        basicIdentity.getUserName(),
                        basicIdentity.getPassword()
                );
                if (jwtToken != null) {
                    return Optional.of(Token.readToken(jwtToken, properties.isMapTokenToSession(), getValidateToken()));
                }
            }
        } catch (Exception e) {
            log.error("error obtaining token", e);
            return Optional.empty();
        }
        return Optional.empty();
    }
}

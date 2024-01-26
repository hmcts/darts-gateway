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
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.ServiceContextCacheValue;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class TokenJwtCache extends AbstractTokenCache implements TokenGeneratable {
    public Predicate<String> validationPredicate;

    @Override
    protected Predicate<String> getValidateToken() {
        if (validationPredicate == null) {
            validationPredicate = validator::validate;
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
    public CacheValue createValue(ServiceContext serviceContext) throws CacheException {
        return new ServiceContextCacheValue(serviceContext);
    }

    @Override
    protected CacheValue getValue(CacheValue holder) throws CacheException {
        return holder;
    }

    @Override
    public Token createToken(ServiceContext context) throws CacheException {
        String jwtToken = "";
        try {
            List<Identity> identities = context.getIdentities();
            if (identities.isEmpty()) {
                throw new CacheTokenCreationException("Could not get an identity in order to fetch a token");
            }

            Identity identity = identities.get(0);
            if (identity instanceof BasicIdentity basicIdentity) {
                jwtToken = generator.acquireNewToken(
                        basicIdentity.getUserName(),
                        basicIdentity.getPassword()
                );
            } else {
                throw new CacheTokenCreationException("Require basic credentials to get a token");
            }
        } catch (Exception e) {
            log.error("error obtaining token", e);
            throw new CacheTokenCreationException("Could not get an identity", e);
        }

        return Token.readToken(jwtToken, properties.isMapTokenToSession(), getValidateToken());
    }

}

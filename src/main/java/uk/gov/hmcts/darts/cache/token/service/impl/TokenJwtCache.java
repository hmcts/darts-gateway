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
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.ServiceContextCacheValue;

import java.util.List;

/**
 * A jwt token cache that maps to {@link uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue} which itself
 * stores and manages a downstream jwt token.
 */
@Slf4j
public class TokenJwtCache extends AbstractTokenCache implements TokenGeneratable {

    private final TokenGenerator generator;

    private final TokenValidator validator;

    public TokenJwtCache(RedisTemplate<String, Object> template, TokenGenerator generator,
                         CacheProperties ctxtregproperties, LockRegistry registry, TokenValidator validator) {
        super(template, registry, ctxtregproperties);
        this.generator = generator;
        this.validator = validator;
    }

    @Override
    protected TokenValidator getTokenValidator() {
        return validator;
    }

    @Override
    public CacheValue createValue(ServiceContext serviceContext) {
        return new RefeshableTokenCacheValue(serviceContext, this);
    }

    @Override
    protected CacheValue getValue(CacheValue holder) {
        return new RefeshableTokenCacheValue((RefeshableTokenCacheValue) holder, this);
    }

    @Override
    public Token createToken(ServiceContext context) {
        List<Identity> identities = context.getIdentities();
        if (identities.isEmpty()) {
            throw new CacheTokenCreationException("Could not get an identity in order to fetch a token");
        }

        Identity identity = identities.get(0);
        if (!(identity instanceof BasicIdentity basicIdentity)) {
            throw new CacheTokenCreationException("Require basic credentials to get a token");
        }

        String jwtToken;
        try {
            jwtToken = generator.acquireNewToken(basicIdentity.getUserName(), basicIdentity.getPassword());
        } catch (Exception e) {
            log.error("error obtaining token", e);
            throw new CacheTokenCreationException("Could not get an identity", e);
        }

        return Token.readToken(jwtToken, properties.isMapTokenToSession(), getTokenValidator());
    }


    @Override
    public String getIdForServiceContext(ServiceContext serviceContext) {
        return ServiceContextCacheValue.getId(serviceContext);
    }
}

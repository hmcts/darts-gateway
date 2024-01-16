package uk.gov.hmcts.darts.cache.token.jwt;

import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.RefreshableCacheValue;
import uk.gov.hmcts.darts.cache.token.ServiceContextCacheValue;
import uk.gov.hmcts.darts.cache.token.Token;
import uk.gov.hmcts.darts.cache.token.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
public class JwtCache extends AbstractTokenCache implements TokenGeneratable {
    public static Predicate<Token> TOKEN_VALIDATION = new Predicate<Token>() {
        @Override
        public boolean test(Token token) {

            // TODO: Validate the jwt here is valid for the next few minutes i.e. enough to make the call

            // validate the token and return a false or truth
            return true;
        }
    };

    @Override
    protected Predicate<Token> getValidateToken() {
        return TOKEN_VALIDATION;
    }

    private final OauthTokenGenerator generator;

    public JwtCache(RedisTemplate<String, Object> template, OauthTokenGenerator generator,
                    CacheProperties ctxtregproperties, LockRegistry registry) {
        super(template, registry, ctxtregproperties);
        this.generator = generator;
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

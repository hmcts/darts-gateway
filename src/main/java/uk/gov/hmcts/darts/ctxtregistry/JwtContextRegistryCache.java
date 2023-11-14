package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;

public class JwtContextRegistryCache extends DefaultContextRegistryCache {
    private final OauthTokenGenerator generator;

    public JwtContextRegistryCache(Cache cacheManager, OauthTokenGenerator generator) {
        super(cacheManager);
        this.generator = generator;
    }

    @Override
    public TokenHolder createToken(ServiceContext context) {
        String jwtToken = generator.acquireNewToken();

        return TokenHolder.generateToken(jwtToken);
    }
}

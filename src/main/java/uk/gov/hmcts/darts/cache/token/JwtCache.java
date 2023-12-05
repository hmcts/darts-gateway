package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

import java.util.Optional;

public class JwtCache extends DefaultCache {
    private final OauthTokenGenerator generator;

    public JwtCache(Cache cacheManager, OauthTokenGenerator generator,
                    ContextRegistryProperties ctxtregproperties) {
        super(cacheManager, ctxtregproperties);
        this.generator = generator;
    }

    @Override
    public Optional<Token> createToken(ServiceContext context) {
        Identity identity = context.getIdentities().get(0);
        String jwtToken = null;
        if (identity instanceof BasicIdentity basicIdentity) {
            jwtToken = generator.acquireNewToken(
                basicIdentity.getUserName(),
                basicIdentity.getPassword()
            );
        }

        if (jwtToken != null) {
            return Optional.of(Token.generateDocumentumToken(jwtToken, properties.isMapTokenToSession()));
        }

        return Optional.empty();
    }

}

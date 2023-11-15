package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.BasicIdentity;
import documentum.contextreg.Identity;
import documentum.contextreg.ServiceContext;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.authentication.config.AuthenticationUserToJwt;
import uk.gov.hmcts.darts.authentication.config.AuthenticationUserToJwtCredentialProperties;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

import java.util.Optional;

public class JwtCache extends DefaultCache {
    private final OauthTokenGenerator generator;

    private final AuthenticationUserToJwtCredentialProperties authenticationUserToJwtCredentialProperties;

    public JwtCache(Cache cacheManager, OauthTokenGenerator generator,
                    ContextRegistryProperties ctxtregproperties,
                    AuthenticationUserToJwtCredentialProperties properties) {
        super(cacheManager, ctxtregproperties);
        this.generator = generator;
        this.authenticationUserToJwtCredentialProperties = properties;
    }

    @Override
    public Optional<Token> createToken(ServiceContext context) {
        Identity identity = context.getIdentities().get(0);
        String jwtToken = null;
        if (identity instanceof BasicIdentity) {

            Optional<AuthenticationUserToJwt> jwtCredentials =
                    authenticationUserToJwtCredentialProperties.getUserToJwtCredentials(((BasicIdentity) identity).getUserName(),
                ((BasicIdentity) identity).getPassword());

            if (jwtCredentials.isPresent()) {
                jwtToken = generator.acquireNewToken(
                    jwtCredentials.get().getJwtUserName(),
                    jwtCredentials.get().getJwtPassword()
                );
            }
        }

        if (jwtToken != null) {
            return Optional.of(Token.generateDocumentumToken(jwtToken, properties.isMapTokenToSession()));
        }

        return Optional.empty();
    }
}

package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.*;
import org.springframework.cache.Cache;
import uk.gov.hmcts.darts.authentication.config.AuthenticationUserToJWT;
import uk.gov.hmcts.darts.authentication.config.AuthenticationUserToJWTCredentialProperties;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

import java.util.Optional;

public class JwtCache extends DefaultCache {
    private final OauthTokenGenerator generator;

    private final AuthenticationUserToJWTCredentialProperties authenticationUserToJWTCredentialProperties;

    public JwtCache(Cache cacheManager, OauthTokenGenerator generator,
                    ContextRegistryProperties ctxtregproperties,
                    AuthenticationUserToJWTCredentialProperties properties) {
        super(cacheManager, ctxtregproperties);
        this.generator = generator;
        this.authenticationUserToJWTCredentialProperties = properties;
    }

    @Override
    public Optional<Token> createToken(ServiceContext context) {
        Identity identity = context.getIdentities().get(0);
        String jwtToken = null;
        if (identity instanceof BasicIdentity) {

            Optional<AuthenticationUserToJWT> jwtCredentials = authenticationUserToJWTCredentialProperties.
                getUserToJWTCredentials(((BasicIdentity) identity).getUserName(),
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

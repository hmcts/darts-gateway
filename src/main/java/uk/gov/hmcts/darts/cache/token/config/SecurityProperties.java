package uk.gov.hmcts.darts.cache.token.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface SecurityProperties {
    String getTokenUri();

    String getScope();

    String getClientId();

    String getJwkSetUri();

    String getSignInPolicy();

    String getIssuerUri();

    String getClaims();

    boolean isUserExternalInternalMappingsEnabled();

    List<ExternalUserToInternalUserMappingImpl> getUserExternalInternalMappings();

    List<String> getExternalServiceBasicAuthorisationWhitelist();

    boolean isUserWhitelisted(String userName);

    default JWKSource<SecurityContext> getJwkSource() throws MalformedURLException {
        URL jwksUrl = new URL(getJwkSetUri());
        return JWKSourceBuilder.create(jwksUrl).build();
    }
}

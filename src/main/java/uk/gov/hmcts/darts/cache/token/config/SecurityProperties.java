package uk.gov.hmcts.darts.cache.token.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;

import java.net.MalformedURLException;
import java.net.URL;

public interface SecurityProperties {
    String getTokenUri();
    String getScope();
    String getClientId();
    String getJwkSetUri();
    String getSignInPolicy();
    String getIssuerUri();
    String getClaims();

    default JWKSource<SecurityContext> getJwkSource() throws MalformedURLException{
        URL jwksUrl = new URL(getJwkSetUri());
        return JWKSourceBuilder.create(jwksUrl).build();
    }
}

package uk.gov.hmcts.darts.cache.token.config;

import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @NonNull
    Duration getJwksCacheRefreshPeriod();

    @NonNull
    Duration getJwksCacheLifetimePeriod();

    default JWKSource<SecurityContext> getJwkSource() throws MalformedURLException {
        URL jwksUrl = new URL(getJwkSetUri());
        return new RemoteJWKSet<>(jwksUrl, null, getJwkCache());
    }

    default JWKSetCache getJwkCache() {
        return new DefaultJWKSetCache(getJwksCacheLifetimePeriod().get(ChronoUnit.SECONDS),
                                      getJwksCacheRefreshPeriod().get(ChronoUnit.SECONDS),
                                      TimeUnit.SECONDS);
    }
}

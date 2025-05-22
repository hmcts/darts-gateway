package uk.gov.hmcts.darts.cache.token.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.NonNull;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public interface SecurityProperties {
    String getTokenUri();

    String getScope();

    String getClientId();

    String getJwkSetUri();

    String getIssuerUri();

    boolean isUserExternalInternalMappingsEnabled();

    List<ExternalUserToInternalUserMappingImpl> getUserExternalInternalMappings();

    boolean isUserWhitelisted(String userName);

    @NonNull
    Duration getJwksCacheRefreshPeriod();

    @NonNull
    Duration getJwksCacheLifetimePeriod();

    @NonNull
    Duration getJwksCacheRefreshAheadTimePeriod();

    default JWKSource<SecurityContext> getJwkSource() throws MalformedURLException {
        URL jwksUrl = URI.create(getJwkSetUri()).toURL();
        return JWKSourceBuilder.create(jwksUrl)
            .rateLimited(false)
            .refreshAheadCache(getJwksCacheRefreshAheadTimePeriod().toMillis(), true)
            .cache(getJwksCacheLifetimePeriod().toMillis(), getJwksCacheRefreshPeriod().toMillis())
            .build();
    }
}

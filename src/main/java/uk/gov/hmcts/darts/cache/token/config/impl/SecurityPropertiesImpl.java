package uk.gov.hmcts.darts.cache.token.config.impl;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

import java.time.Duration;
import java.util.List;

@Component
@ConfigurationProperties("darts-gateway.security")
@Getter
@Setter
@Validated
public class SecurityPropertiesImpl implements SecurityProperties {
    private String tokenUri;
    private String scope;
    private String clientId;
    private String jwkSetUri;
    private String signInPolicy;
    private String issuerUri;
    private String claims;

    @jakarta.validation.constraints.NotNull
    private Duration jwksCacheRefreshPeriod;

    @jakarta.validation.constraints.NotNull
    private Duration jwksCacheLifetimePeriod;

    private boolean userExternalInternalMappingsEnabled;
    private List<ExternalUserToInternalUserMappingImpl> userExternalInternalMappings;
    private List<String> externalServiceBasicAuthorisationWhitelist;

    @Override
    public boolean isUserWhitelisted(String userName) {
        return externalServiceBasicAuthorisationWhitelist.contains(userName);
    }
}

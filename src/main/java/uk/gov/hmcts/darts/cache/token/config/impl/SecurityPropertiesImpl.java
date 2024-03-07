package uk.gov.hmcts.darts.cache.token.config.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

import java.util.List;

@Component
@ConfigurationProperties("darts-gateway.security")
@Getter
@Setter
public class SecurityPropertiesImpl implements SecurityProperties {
    private String tokenUri;
    private String scope;
    private String clientId;
    private String jwkSetUri;
    private String signInPolicy;
    private String issuerUri;
    private String claims;

    private boolean userExternalInternalMappingsEnabled;
    private List<ExternalUserToInternalUserMappingImpl> userExternalInternalMappings;
    private List<String> externalServiceBasicAuthorisationWhitelist;
}

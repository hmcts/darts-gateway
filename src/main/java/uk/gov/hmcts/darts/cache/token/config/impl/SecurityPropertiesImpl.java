package uk.gov.hmcts.darts.cache.token.config.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

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
}
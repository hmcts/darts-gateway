package uk.gov.hmcts.darts.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "darts-gateway")
@Getter
@Setter
public class FunctionalProperties {
    private ExternalUserToInternalUserMappingImpl viq;

    private ExternalUserToInternalUserMappingImpl cpp;

    private ExternalUserToInternalUserMappingImpl xhibit;

    private URI deployedApplicationUri;

    private URI dartsApi;
}

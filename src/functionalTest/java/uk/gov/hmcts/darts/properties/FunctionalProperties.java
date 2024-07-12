package uk.gov.hmcts.darts.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ConfigurationProperties(prefix = "darts-gateway")
@Getter
@Setter
@Slf4j
public class FunctionalProperties {
    private ExternalUserToInternalUserMappingImpl viq;

    private ExternalUserToInternalUserMappingImpl cpp;

    private ExternalUserToInternalUserMappingImpl xhibit;

    private URI deployedApplicationUri;

    private URI dartsApi;

    public URI getDeployedApplicationUri() {
        URI url = deployedApplicationUri;
        try {
            url = new URI(deployedApplicationUri.toString().replace("****", "darts"));
        } catch (URISyntaxException malformedUrlException) {
            log.error("Could not substitute", malformedUrlException);
        }

        return url;
    }
}

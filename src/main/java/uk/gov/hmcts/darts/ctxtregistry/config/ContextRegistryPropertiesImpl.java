package uk.gov.hmcts.darts.ctxtregistry.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("darts-gateway.context-registry")
@Getter
@Setter
public class ContextRegistryPropertiesImpl implements ContextRegistryProperties {
    private String tokenGenerate;

    private boolean mapTokenToSession;
}

package uk.gov.hmcts.darts.cache.token.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("darts-gateway.cache")
@Getter
@Setter
public class CachePropertiesImpl implements CacheProperties {
    private String tokenGenerate;

    private boolean mapTokenToSession;

    private boolean shareTokenForSameCredentials;

    private long entryTimeToIdleSeconds;
}

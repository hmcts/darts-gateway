package uk.gov.hmcts.darts.cache.token.config.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;

@Component
@ConfigurationProperties("darts-gateway.cache")
@Getter
@Setter
public class CachePropertiesImpl implements CacheProperties {
    private String tokenGenerate;

    private boolean mapTokenToSession;

    private boolean shareTokenForSameCredentials;

    private long entryTimeToIdleSeconds;

    private int sharedTokenEarlyExpirationMinutes;
}

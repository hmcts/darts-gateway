package uk.gov.hmcts.darts.cache.token.config;

import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

public class KeyConfiguration extends KeyspaceConfiguration {

    private final CacheProperties properties;

    @SuppressWarnings("PMD.CallSuperInConstructor")
    public KeyConfiguration(CacheProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean hasSettingsFor(Class<?> type) {
        return true;
    }

    @Override
    public KeyspaceSettings getKeyspaceSettings(Class<?> type) {

        KeyspaceSettings keyspaceSettings = new KeyspaceSettings(type, "my-keyspace");
        keyspaceSettings.setTimeToLive(properties.getEntryTimeToIdleSeconds());

        return keyspaceSettings;
    }
}

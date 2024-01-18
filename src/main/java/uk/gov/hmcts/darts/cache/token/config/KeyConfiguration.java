package uk.gov.hmcts.darts.cache.token.config;

import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

public class KeyConfiguration extends KeyspaceConfiguration {

    private final CacheProperties properties;

    public KeyConfiguration(CacheProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean hasSettingsFor(Class<?> type) {
        return true;
    }

    @Override
    public org.springframework.data.redis.core.convert.KeyspaceConfiguration.KeyspaceSettings getKeyspaceSettings(Class<?> type) {

        org.springframework.data.redis.core.convert.KeyspaceConfiguration.KeyspaceSettings keyspaceSettings
            = new org.springframework.data.redis.core.convert.KeyspaceConfiguration.KeyspaceSettings(type, "my-keyspace");
        keyspaceSettings.setTimeToLive(properties.getEntryTimeToIdleSeconds());

        return keyspaceSettings;
    }
}

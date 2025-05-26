package uk.gov.hmcts.darts.cache.token.config;

public interface CacheProperties {
    long getEntryTimeToIdleSeconds();

    int getSharedTokenEarlyExpirationMinutes();
}

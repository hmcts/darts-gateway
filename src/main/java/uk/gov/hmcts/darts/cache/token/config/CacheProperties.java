package uk.gov.hmcts.darts.cache.token.config;

public interface CacheProperties {
    boolean isMapTokenToSession();

    boolean isShareTokenForSameCredentials();

    long getEntryTimeToIdleSeconds();

    int getSharedTokenEarlyExpirationMinutes();
}

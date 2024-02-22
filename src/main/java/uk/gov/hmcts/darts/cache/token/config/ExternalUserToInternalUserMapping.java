package uk.gov.hmcts.darts.cache.token.config;

public interface ExternalUserToInternalUserMapping {
    String getUserName();

    String getExternalPassword();

    String getInternalPassword();
}

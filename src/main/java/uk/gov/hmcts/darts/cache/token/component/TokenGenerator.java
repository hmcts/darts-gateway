package uk.gov.hmcts.darts.cache.token.component;

public interface TokenGenerator {
     String acquireNewToken(String username, String password);
}
package uk.gov.hmcts.darts.cache.token.component;

@FunctionalInterface
public interface TokenGenerator {
    String acquireNewToken(String username, String password);
}

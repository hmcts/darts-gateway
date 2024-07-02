package uk.gov.hmcts.darts.cache.token.exception;

public class CacheTokenValidationException extends CacheException {
    public CacheTokenValidationException(String message, Exception ex) {
        super(message, ex);
    }
}

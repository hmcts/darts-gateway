package uk.gov.hmcts.darts.cache.token.exception;

public class CacheTokenValidationException extends CacheException {
    public CacheTokenValidationException(Throwable cause) {
        super("Can not validate the token", cause);
    }

    public CacheTokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheTokenValidationException(String message) {
        super(message);
    }
}

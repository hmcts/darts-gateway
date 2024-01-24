package uk.gov.hmcts.darts.cache.token.exception;

public class CacheTokenCreationException extends CacheException {
    public CacheTokenCreationException(Throwable cause) {
        super("Can not create a token", cause);
    }

    public CacheTokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheTokenCreationException(String message) {
        super(message);
    }
}

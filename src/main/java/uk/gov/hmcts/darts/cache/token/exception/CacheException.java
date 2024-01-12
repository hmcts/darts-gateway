package uk.gov.hmcts.darts.cache.token.exception;

public class CacheException extends RuntimeException {
    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(String message) {
        super(message);
    }
}

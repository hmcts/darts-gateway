package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.util.Optional;

public interface TokenRegisterable {
    Optional<Token> store(RefreshableCacheValue value) throws CacheException;

    Optional<Token> store(RefreshableCacheValue value, Boolean reuseTokenIfPossible) throws CacheException;

    Optional<RefreshableCacheValue> lookup(Token holder) throws CacheException;

    Optional<Token> lookup(RefreshableCacheValue context) throws CacheException;

    void evict(Token holder) throws CacheException;

    Token getToken(String token) throws CacheException;

    RefreshableCacheValue createValue(ServiceContext serviceContext) throws CacheException;


}

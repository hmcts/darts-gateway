package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.util.Optional;

public interface TokenRegisterable {
    Optional<Token> store(CacheValue value) throws CacheException;

    Optional<Token> store(CacheValue value, Boolean reuseTokenIfPossible) throws CacheException;

    Optional<CacheValue> lookup(Token holder) throws CacheException;

    void evict(Token holder) throws CacheException;

    Token getToken(String token) throws CacheException;

    CacheValue createValue(ServiceContext serviceContext) throws CacheException;


}

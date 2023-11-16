package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;

import java.util.Optional;

public interface TokenRegisterable {
    void store(Token holder, RefreshableCacheValue value);

    RefreshableCacheValue lookup(Token holder, boolean refresh);

    void evict(Token holder);

    Optional<Token> createToken(ServiceContext context);

    Optional<Token> getToken(String token);

    RefreshableCacheValue createValue(ServiceContext serviceContext);


}

package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;

public interface TokenRegisterable {
    void store(TokenHolder holder, RefreshableCacheValue value);

    RefreshableCacheValue lookup(TokenHolder holder);

    void evict(TokenHolder holder);

    TokenHolder createToken(ServiceContext context);

    RefreshableCacheValue createValue(ServiceContext serviceContext);
}

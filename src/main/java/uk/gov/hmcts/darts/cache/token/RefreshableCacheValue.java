package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

public interface RefreshableCacheValue {
    String getContextString() throws CacheException;

    String getId() throws CacheException;

    ServiceContext getServiceContext() throws CacheException;

    boolean refresh() throws CacheException;

    void performRefresh() throws CacheException;

    String getDownstreamToken();

    default boolean hasToken() {
        return !getDownstreamToken().isEmpty();
    }
}

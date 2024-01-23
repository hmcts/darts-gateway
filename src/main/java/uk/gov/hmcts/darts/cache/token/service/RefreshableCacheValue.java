package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

public interface RefreshableCacheValue {
    String getContextString() throws CacheException;

    String getId() throws CacheException;

    ServiceContext getServiceContext() throws CacheException;

    boolean refresh() throws CacheException;

    void performRefresh() throws CacheException;

}

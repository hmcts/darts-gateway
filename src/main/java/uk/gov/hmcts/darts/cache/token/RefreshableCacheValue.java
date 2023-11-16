package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;

public interface RefreshableCacheValue {
    ServiceContext getContext();

    /**
     * The ability for a cached value to be refreshed e.g. jwt refresh etc
     */
    void refresh();
}

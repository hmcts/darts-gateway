package uk.gov.hmcts.darts.cache.token.service.value;

import documentum.contextreg.ServiceContext;

/**
 * A cache value that can be stored in the cache. The default cache expects to store a service context xml block
 */
public interface CacheValue {
    String getContextString();

    String getSharedKey();

    //service context from the consumer
    ServiceContext getServiceContext();
}

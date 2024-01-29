package uk.gov.hmcts.darts.cache.token.service.value;

import documentum.contextreg.ServiceContext;

public interface CacheValue {
    String getContextString();

    String getId();

    ServiceContext getServiceContext();
}

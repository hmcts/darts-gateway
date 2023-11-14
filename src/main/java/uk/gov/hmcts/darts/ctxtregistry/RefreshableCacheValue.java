package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;

public interface RefreshableCacheValue {
    ServiceContext getContext();

    void refresh();
}

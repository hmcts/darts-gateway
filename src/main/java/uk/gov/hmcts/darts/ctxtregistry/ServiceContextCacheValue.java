package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceContextCacheValue implements RefreshableCacheValue {

    private final ServiceContext context;

    @Override
    public void refresh() {

    }

    @Override
    public ServiceContext getContext() {
        return context;
    }
}

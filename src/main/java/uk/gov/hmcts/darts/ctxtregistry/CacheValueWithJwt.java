package uk.gov.hmcts.darts.ctxtregistry;

import documentum.contextreg.ServiceContext;

// TODO: A mapping between the cached security context and the JWT. This will be implemented in another ticket
public class CacheValueWithJwt extends ServiceContextCacheValue {
    public CacheValueWithJwt(ServiceContext context) {
        super(context);

        refresh();
    }

    @Override
    public void refresh() {
        //TODO get hold of a new JWT if it has expired
    }
}

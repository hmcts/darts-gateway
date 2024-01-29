package uk.gov.hmcts.darts.cache.token.service;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

public interface TokenGeneratable {
    Token createToken(ServiceContext context) throws CacheException;

    Token getToken(String token);
}

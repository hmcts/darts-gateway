package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.ServiceContext;
import uk.gov.hmcts.darts.cache.token.exception.CacheException;

import java.util.Optional;

public interface TokenGeneratable {
    Optional<Token> createToken(ServiceContext context) throws CacheException;

    Token getToken(String token);
}

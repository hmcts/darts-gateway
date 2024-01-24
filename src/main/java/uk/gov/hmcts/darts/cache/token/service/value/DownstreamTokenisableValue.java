package uk.gov.hmcts.darts.cache.token.service.value;

import uk.gov.hmcts.darts.cache.token.exception.CacheException;
import uk.gov.hmcts.darts.cache.token.service.Token;

import java.util.Optional;

public interface DownstreamTokenisableValue extends CacheValue {

    Optional<Token> getValidatedToken();

    String getDownstreamToken();

    void setDownstreamToken(String token);

    boolean refresh() throws CacheException;

    void performRefresh() throws CacheException;
}

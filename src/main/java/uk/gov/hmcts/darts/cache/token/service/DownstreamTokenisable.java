package uk.gov.hmcts.darts.cache.token;

import java.util.Optional;

public interface DownstreamTokenisable {

    Optional<Token> getValidatedToken();

    String getDownstreamToken();

    void setDownstreamToken(String token);
}

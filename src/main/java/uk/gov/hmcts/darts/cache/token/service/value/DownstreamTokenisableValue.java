package uk.gov.hmcts.darts.cache.token.service.value;

import uk.gov.hmcts.darts.cache.token.service.Token;

/**
 * Defines a cache value that has the ability to store an authenticated token for downstream communication.
 */
public interface DownstreamTokenisableValue extends CacheValue {

    /**
     * gets the validated token.
     * @return if the token is not valid we return an empty value else the token
     */
    Token getToken();

    /**
     * Gets the string representation of the token.
     * @return The token
     */
    String getDownstreamToken();

    void setDownstreamToken(String token);

    /**
     * Determine if the internal downstream token is in need of a refresh i.e. it has expired.
     * @return True or false
     */
    boolean doesRequireRefresh();

    /**
     * performs the internal refresh of the downstream token.
     */
    void performRefresh();
}

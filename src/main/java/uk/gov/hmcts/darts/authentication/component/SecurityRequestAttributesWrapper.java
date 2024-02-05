package uk.gov.hmcts.darts.authentication.component;

import org.springframework.web.context.request.RequestAttributes;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class SecurityRequestAttributesWrapper {

    public static final String ACCESS_TOKEN_REQUEST_ATTR = "access_token";

    private final RequestAttributes requestAttributes;

    public SecurityRequestAttributesWrapper(RequestAttributes attributes) {
        this.requestAttributes = attributes;
    }

    public String getAuthenticationToken() {
        Object accessTokenObj =  requestAttributes.getAttribute(ACCESS_TOKEN_REQUEST_ATTR, SCOPE_REQUEST);

        String tokenToReturn = "";

        // if we have a token that can be refreshed and it needs refreshing then refresh it
        if (accessTokenObj instanceof DownstreamTokenisableValue tokenCacheValue) {
            if (tokenCacheValue.refresh()) {
                tokenCacheValue.performRefresh();
                tokenToReturn = tokenCacheValue.getDownstreamToken();
            } else {
                tokenToReturn = tokenCacheValue.getDownstreamToken();
            }
        } else if (accessTokenObj instanceof String accessToken) {
            tokenToReturn = accessToken;
        } else {
            throw new DartsValidationException("Authorization Bearer Header missing JWT", CodeAndMessage.ERROR);
        }

        return tokenToReturn;
    }

    public void setAuthenticationToken(String token) {
        requestAttributes.setAttribute(
            ACCESS_TOKEN_REQUEST_ATTR,
            token,
            SCOPE_REQUEST
        );
    }

    public void setAuthenticationToken(DownstreamTokenisableValue token) {
        requestAttributes.setAttribute(
            ACCESS_TOKEN_REQUEST_ATTR,
            token,
            SCOPE_REQUEST
        );
    }
}

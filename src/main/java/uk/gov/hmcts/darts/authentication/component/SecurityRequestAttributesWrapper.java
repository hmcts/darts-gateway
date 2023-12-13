package uk.gov.hmcts.darts.authentication.component;

import org.springframework.web.context.request.RequestAttributes;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class SecurityRequestAttributesWrapper {

    private static final String ACCESS_TOKEN_REQUEST_ATTR = "access_token";

    private final RequestAttributes requestAttributes;

    public SecurityRequestAttributesWrapper(RequestAttributes attributes) {
        this.requestAttributes = attributes;
    }

    public String getAuthenticationToken() {
        Object accessTokenObj =  requestAttributes.getAttribute(ACCESS_TOKEN_REQUEST_ATTR, SCOPE_REQUEST);
        if (accessTokenObj instanceof String accessToken) {
            return accessToken;
        } else {
            throw new DartsValidationException("Authorization Bearer Header missing JWT", CodeAndMessage.ERROR);
        }
    }

    public void setAuthenticationToken(String token) {
        requestAttributes.setAttribute(
            ACCESS_TOKEN_REQUEST_ATTR,
            token,
            SCOPE_REQUEST
        );
    }
}

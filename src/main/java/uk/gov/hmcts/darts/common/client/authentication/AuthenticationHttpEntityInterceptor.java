package uk.gov.hmcts.darts.common.client.authentication;


import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import uk.gov.hmcts.darts.authentication.component.SecurityRequestAttributesWrapper;
import uk.gov.hmcts.darts.common.client.component.HttpHeadersInterceptor;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthenticationHttpEntityInterceptor implements HttpHeadersInterceptor {
    @Override
    public void accept(HttpHeaders headers) {
        String authenticationToken = new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).getAuthenticationToken();

        headers.set(AUTHORIZATION, "Bearer " + authenticationToken);
    }
}

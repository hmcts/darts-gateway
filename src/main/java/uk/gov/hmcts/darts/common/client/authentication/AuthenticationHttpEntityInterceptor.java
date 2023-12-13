package uk.gov.hmcts.darts.common.client.authentication.impl;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import uk.gov.hmcts.darts.authentication.component.RequestAttributesWrapper;
import uk.gov.hmcts.darts.common.client.component.HttpHeadersInterceptor;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthenticationHttpEntityInterceptor implements HttpHeadersInterceptor {
    @Override
    public void accept(HttpEntity httpEntity) {
        String authenticationToken = new RequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).getAuthenticationToken();

        httpEntity.getHeaders().set(AUTHORIZATION, "Bearer " + authenticationToken);
    }
}

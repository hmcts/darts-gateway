package uk.gov.hmcts.darts.authentication.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String authenticationToken = new SecurityRequestAttributesWrapper(RequestContextHolder.currentRequestAttributes()).getAuthenticationToken();

        template.header(AUTHORIZATION, "Bearer " + authenticationToken);
    }
}

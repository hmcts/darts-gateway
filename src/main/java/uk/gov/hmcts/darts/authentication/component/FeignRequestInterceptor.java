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

    private static final String ACCESS_TOKEN_REQUEST_ATTR = "access_token";

    @Override
    public void apply(RequestTemplate template) {
        Object accessTokenObj = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
            .getAttribute(ACCESS_TOKEN_REQUEST_ATTR);
        if (accessTokenObj instanceof String accessToken) {
            template.header(AUTHORIZATION, "Bearer " + accessToken);
        } else {
            throw new DartsValidationException("Authorization Bearer Header missing JWT", CodeAndMessage.ERROR);
        }
    }


}

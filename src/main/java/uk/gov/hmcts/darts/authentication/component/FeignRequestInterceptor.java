package uk.gov.hmcts.darts.authentication.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Profile("!int-test")
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final String ACCESS_TOKEN_REQUEST_ATTR = "access_token";

    @Override
    public void apply(RequestTemplate template) {
        Object accessTokenObj = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
            .getAttribute(ACCESS_TOKEN_REQUEST_ATTR);
        if (accessTokenObj != null) {
            template.header("Authorization", "Bearer " + (String) accessTokenObj);
        }
    }

}

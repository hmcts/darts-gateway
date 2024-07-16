package uk.gov.hmcts.darts.common.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * If we use the feign interface to drive the darts api we can store the token here so it is used.
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    private static String tokenToUse;

    @Override
    public void apply(RequestTemplate template) {
        template.header(AUTHORIZATION, "Bearer " + tokenToUse);
    }
}

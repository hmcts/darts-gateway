package uk.gov.hmcts.darts.authentication.component;

import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

class FeignRequestInterceptorTest {

    private MockHttpServletRequest mockHttpServletRequest;
    private RequestTemplate requestTemplate;

    private final FeignRequestInterceptor feignRequestInterceptor = new FeignRequestInterceptor();

    @BeforeEach
    void beforeEach() {
        mockHttpServletRequest = new MockHttpServletRequest("POST", "/test");
        requestTemplate = new RequestTemplate();
    }

    @Test
    void givenAccessTokenRequestAttributeIsSet_whenFeignRequestInterceptorCalledForRequest_thenAuthorizationHeaderIsAdded() {
        mockHttpServletRequest.setAttribute("access_token", "testJWT");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        feignRequestInterceptor.apply(requestTemplate);

        assertTrue(requestTemplate.headers().containsKey(AUTHORIZATION));
    }

    @Test
    void givenAccessTokenRequestAttributeIsNotSet_whenFeignRequestInterceptorCalledForRequest_thenAuthorizationHeaderIsNotAdded() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        feignRequestInterceptor.apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey(AUTHORIZATION));
    }

}

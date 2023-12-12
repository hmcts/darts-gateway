package uk.gov.hmcts.darts.authentication.component;

import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void givenAccessTokenRequestAttributeIsNotSet_whenFeignRequestInterceptorCalledForRequest_thenDartsValidationExceptionIsThrown() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));

        Exception exception = assertThrows(DartsValidationException.class, () -> feignRequestInterceptor.apply(requestTemplate));
        assertEquals("Authorization Bearer Header missing JWT", exception.getMessage());
    }

}

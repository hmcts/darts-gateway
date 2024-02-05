package uk.gov.hmcts.darts.authentication.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestAttributes;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;

class SecurityRequestAttributesWrapperTest {

    private SecurityRequestAttributesWrapper securityRequestAttributesWrapper;

    private RequestAttributes atts;

    @BeforeEach
    public void beforeEach() {
        atts = Mockito.mock(RequestAttributes.class);
        securityRequestAttributesWrapper = new SecurityRequestAttributesWrapper(atts);
    }

    @Test
    void testGettingADownstreamTokenForRefresh() {
        DownstreamTokenisableValue downstreamTokenisableValue = Mockito.mock(DownstreamTokenisableValue.class);

        String downstreamToken = "refreshedToken";
        Mockito.when(downstreamTokenisableValue.getDownstreamToken()).thenReturn(downstreamToken);
        Mockito.when(downstreamTokenisableValue.refresh()).thenReturn(true);

        Mockito.when(atts.getAttribute(SecurityRequestAttributesWrapper.ACCESS_TOKEN_REQUEST_ATTR, 0))
            .thenReturn(downstreamTokenisableValue);

        Assertions.assertEquals(downstreamToken, securityRequestAttributesWrapper.getAuthenticationToken());

        Mockito.verify(downstreamTokenisableValue).performRefresh();
    }

    @Test
    void testGettingADownstreamTokenNoNeedToRefresh() {
        DownstreamTokenisableValue downstreamTokenisableValue = Mockito.mock(DownstreamTokenisableValue.class);

        String downstreamToken = "refreshedToken";
        Mockito.when(downstreamTokenisableValue.getDownstreamToken()).thenReturn(downstreamToken);
        Mockito.when(downstreamTokenisableValue.refresh()).thenReturn(false);

        Mockito.when(atts.getAttribute(SecurityRequestAttributesWrapper.ACCESS_TOKEN_REQUEST_ATTR, 0))
            .thenReturn(downstreamTokenisableValue);

        Assertions.assertEquals(downstreamToken, securityRequestAttributesWrapper.getAuthenticationToken());

        Mockito.verify(downstreamTokenisableValue, Mockito.times(0)).performRefresh();
    }

    @Test
    void testGettingToken() {
        String downstreamToken = "refreshedToken";

        Mockito.when(atts.getAttribute(SecurityRequestAttributesWrapper.ACCESS_TOKEN_REQUEST_ATTR, 0))
            .thenReturn(downstreamToken);

        Assertions.assertEquals(downstreamToken, securityRequestAttributesWrapper.getAuthenticationToken());
    }
}

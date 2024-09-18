package uk.gov.hmcts.darts.authentication.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Base64;

class SoapRequestInterceptorTest {

    @Test
    void testCookieAndSessionMatch() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        String requestSessionInBase64 = "YjdiODRlY2YtMmEwNC00MDgyLWI3ODktM2M1M2M1N2M3NzU1";
        String sessionIdDecoded = new String(Base64.getDecoder().decode(requestSessionInBase64.getBytes()));
        String cookieInRequest = "JSESSIONID=" + requestSessionInBase64;
        Mockito.when(request.getHeader("Cookie")).thenReturn(cookieInRequest);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(sessionIdDecoded);
        String prefix = "prefix {}";
        String cookieInformation = SoapRequestInterceptor.getCookieInformation(prefix, request).trim();
        Assertions.assertEquals("Using the same session as the inbound cookie " + sessionIdDecoded, cookieInformation);
    }

    @Test
    void testExampleCppCookie() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        String requestSessionInBase64 = "MDQzNzVkNDEtZTdkNi00MzUzLWE5NWItZjBjNzFkZmU2ZTFl";
        String sessionIdDecoded = new String(Base64.getDecoder().decode(requestSessionInBase64.getBytes()));
        String cookieInRequest = "ROUTEID=.0;SameSite=Lax;JSESSIONID=MDQzNzVkNDEtZTdkNi00MzUzLWE5NWItZjBjNzFkZmU2ZTFl;";
        Mockito.when(request.getHeader("Cookie")).thenReturn(cookieInRequest);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(sessionIdDecoded);
        String prefix = "prefix {}";
        String cookieInformation = SoapRequestInterceptor.getCookieInformation(prefix, request).trim();
        Assertions.assertEquals("Using the same session as the inbound cookie " + sessionIdDecoded, cookieInformation);
    }

    @Test
    void testInvalidCookie() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        String requestSessionInBase64 = "MDQzNzVkNDEtZTdkNi00MzUzLWE5NWItZjBjNzFkZmU2ZTFl";
        String sessionIdDecoded = new String(Base64.getDecoder().decode(requestSessionInBase64.getBytes()));
        String cookieInRequest = "ROUTEID=.0;SameSite=Lax;JSESSIONID=MDQzNzVkNDEtZTdkNi00MzUzLWE5NWItZjBjNzFkZmU2ZTFl1;";
        Mockito.when(request.getHeader("Cookie")).thenReturn(cookieInRequest);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(sessionIdDecoded);
        String prefix = "prefix {}";
        String cookieInformation = SoapRequestInterceptor.getCookieInformation(prefix, request).trim();
        Assertions.assertTrue(cookieInformation.contains("An inbound cookie was present but not found. A new cookie was generated."));
    }

    @Test
    void testCookieAndSessionDoNotMatch() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        String requestSessionInBase64 = "YjdiODRlY2YtMmEwNC00MDgyLWI3ODktM2M1M2M1N2M3NzU1";
        String serverSessionId = new String(Base64.getDecoder().decode(requestSessionInBase64.getBytes())) + "diff";

        String cookieInRequest = "JSESSIONID=" + requestSessionInBase64;
        Mockito.when(request.getHeader("Cookie")).thenReturn(cookieInRequest);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(
            serverSessionId);
        String prefix = "prefix {}";
        String requestSessionIdDecoded = new String(Base64.getDecoder().decode(requestSessionInBase64.getBytes()));
        String cookieInformation = SoapRequestInterceptor.getCookieInformation(prefix, request).trim();
        Assertions.assertEquals("An inbound cookie was present but not found. A new cookie was generated. Inbound: "
                                    + requestSessionIdDecoded + " Outbound Set-Cookie: " + serverSessionId, cookieInformation);
    }

    @Test
    void testNoCookieSendExpectingBrandNewSession() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        String sessionInBase64 = "YjdiODRlY2YtMmEwNC00MDgyLWI3ODktM2M1M2M1N2M3NzU1";
        String sessionIdDecoded = new String(Base64.getDecoder().decode(sessionInBase64.getBytes()));
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getId()).thenReturn(sessionIdDecoded);
        String prefix = "prefix {}";
        String cookieInformation = SoapRequestInterceptor.getCookieInformation(prefix, request).trim();
        Assertions.assertEquals("Header Details - Set-Cookie : " +  sessionIdDecoded, cookieInformation);
    }

    @Test
    void testNoCookieAndNoSessionGenerated() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getSession()).thenReturn(null);
        String prefix = "prefix {}";
        String cookieInformation = SoapRequestInterceptor.getCookieInformation(prefix, request).trim();
        Assertions.assertEquals("An inbound cookie was not found and an outbound cookie was not set", cookieInformation);
    }
}

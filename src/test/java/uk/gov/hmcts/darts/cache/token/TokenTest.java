package uk.gov.hmcts.darts.cache.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

class TokenTest {

    private static final String SESSION_ID = "testSession";
    private static final String EXISTING_SESSION_ID = "testSessionExisting";

    @BeforeAll
    static void before() {
        MockedStatic<RequestContextHolder> contextHolder = Mockito.mockStatic(RequestContextHolder.class);
        ServletRequestAttributes attributes = Mockito.mock(ServletRequestAttributes.class);
        contextHolder.when(() -> RequestContextHolder.currentRequestAttributes()).thenReturn(attributes);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        HttpSession existingSession = Mockito.mock(HttpSession.class);

        Mockito.when(attributes.getRequest()).thenReturn(request);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(existingSession);

        Mockito.when(session.getId()).thenReturn(SESSION_ID);
        Mockito.when(existingSession.getId()).thenReturn(EXISTING_SESSION_ID);
    }

    @Test
    void createTokenWithSession() {
        Token token = Token.generateDocumentumToken(true);
        Assertions.assertNotNull(token.getToken());
        Assertions.assertEquals(SESSION_ID, token.getSessionId());
    }

    @Test
    void createTokenWithNoSession() {
        Token token = Token.generateDocumentumToken(false);
        Assertions.assertNotNull(token.getToken());
        Assertions.assertNull(token.getSessionId());
    }

    @Test
    void createTokenWithStringAndNoSession() {
        String tokenStr = "token";
        Token token = Token.generateDocumentumToken(tokenStr, false);
        Assertions.assertEquals(tokenStr,token.getToken());
        Assertions.assertNull(token.getSessionId());
    }

    @Test
    void createTokenWithStringAndSession() {
        String tokenStr = "token";
        Token token = Token.generateDocumentumToken(tokenStr, true);
        Assertions.assertEquals(tokenStr,token.getToken());
        Assertions.assertNotNull(token.getSessionId());
    }

    @Test
    void readTokenWithSession() {
        String tokenStr = "token";
        Optional<Token> token = Token.readToken(tokenStr, true);
        Assertions.assertEquals(tokenStr,token.get().getToken());
        Assertions.assertEquals(EXISTING_SESSION_ID, token.get().getSessionId());
    }

    @Test
    void readTokenWithNoSession() {
        String tokenStr = "token";
        Optional<Token> token = Token.readToken(tokenStr, false);
        Assertions.assertEquals(tokenStr,token.get().getToken());
        Assertions.assertNull(token.get().getSessionId());
    }
}

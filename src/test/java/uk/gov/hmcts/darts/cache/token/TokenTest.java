package uk.gov.hmcts.darts.cache.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.service.Token;

import static org.mockito.Mockito.verify;

class TokenTest {

    private static final String SESSION_ID = "testSession";
    private static final String EXISTING_SESSION_ID = "testSessionExisting";

    private static MockedStatic<RequestContextHolder> contextHolder;

    @BeforeAll
    static void before() {
        contextHolder = Mockito.mockStatic(RequestContextHolder.class);
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

    @AfterAll
    public static void close() {
        contextHolder.close();
    }

    @Test
    void createTokenWithSession() {
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Token token = Token.generateDocumentumToken(true, validate);
        Assertions.assertNotNull(token.getTokenString());
        Assertions.assertEquals(EXISTING_SESSION_ID, token.getSessionId());

        verify(validate).test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull());
    }

    @Test
    void createTokenWithNoSession() {

        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Token token = Token.generateDocumentumToken(false, validate);
        Assertions.assertNotNull(token.getTokenString());
        Assertions.assertTrue(token.getSessionId().isEmpty());
    }

    @Test
    void createTokenWithStringAndNoSession() {
        String tokenStr = "token";
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY, tokenStr)).thenReturn(true);

        Token token = Token.readToken(tokenStr, false, validate);
        Assertions.assertEquals(tokenStr,token.getTokenString().get());
        Assertions.assertTrue(token.getSessionId().isEmpty());

        verify(validate).test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull());
    }

    @Test
    void createTokenWithStringAndSession() {
        String tokenStr = "token";
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY, tokenStr)).thenReturn(true);
        Token token = Token.readToken(tokenStr, true, validate);
        Assertions.assertFalse(token.getSessionId().isEmpty());
        Assertions.assertNotNull(token.getTokenString());
    }

    @Test
    void createTokenWithStringAndSessionValidateFalse() {
        String tokenStr = "token";
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY, tokenStr)).thenReturn(false);
        Token token = Token.readToken(tokenStr, true, validate);
        Assertions.assertFalse(token.getSessionId().isEmpty());
        Assertions.assertTrue(token.getTokenString().isEmpty());
    }

    @Test
    void readTokenWithSession() {
        String tokenStr = "token";
        Token token = Token.readToken(tokenStr, true, null);
        Assertions.assertEquals(tokenStr,token.getTokenString().get());
        Assertions.assertEquals(EXISTING_SESSION_ID, token.getSessionId());
    }

    @Test
    void readTokenWithNoSession() {
        String tokenStr = "token";
        Token token = Token.readToken(tokenStr, false, null);
        Assertions.assertEquals(tokenStr,token.getTokenString().get());
        Assertions.assertTrue(token.getSessionId().isEmpty());
    }

    @Test
    void createTokenWithStringAndSessionNoValidation() {
        String tokenStr = "token";
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY, tokenStr)).thenReturn(true);
        Token token = Token.readToken(tokenStr, true, validate);
        Assertions.assertFalse(token.getSessionId().isEmpty());
        Assertions.assertNotNull(token.getTokenString(false));

        verify(validate, Mockito.times(0)).test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull());
    }

    @Test
    void validateTokenTrue() {
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Token token = Token.generateDocumentumToken(true, validate);
        Assertions.assertTrue(token.valid());
        verify(validate, Mockito.times(1)).test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull());
    }

    @Test
    void validateTokenFalse() {
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(false);
        Token token = Token.generateDocumentumToken(true, validate);
        Assertions.assertFalse(token.valid());
        verify(validate, Mockito.times(1)).test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull());
    }

    @Test
    void validateTokenWithOffset() {
        TokenValidator validate = Mockito.mock(TokenValidator.class);
        Mockito.when(validate.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Token token = Token.generateDocumentumToken(true, validate);
        Assertions.assertTrue(token.valid(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY));
        verify(validate, Mockito.times(1)).test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull());
    }

}

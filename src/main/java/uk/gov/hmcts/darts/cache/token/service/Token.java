package uk.gov.hmcts.darts.cache.token.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@EqualsAndHashCode
@Slf4j
public class Token {

    private final String tokenString;

    private String sessionId = "";

    public static final AtomicLong COUNTER = new AtomicLong();

    public enum TokenExpiryEnum {
        DO_NOT_APPLY_EARLY_TOKEN_EXPIRY, APPLY_EARLY_TOKEN_EXPIRY
    }

    private final TokenValidator validator;

    Token(String token,  TokenValidator validator) {
        this.tokenString = token;
        this.validator = validator;
    }

    public String getTokenString() {
        return tokenString;
    }

    @EqualsAndHashCode.Include
    public String getKey() {
        return TokenRegisterable.CACHE_PREFIX + ":" + tokenString + ":" + sessionId;
    }


    /**Ω
     * validates token taking into account the expiry offset if it is enabled.
     * See {@link uk.gov.hmcts.darts.cache.token.config.CacheProperties#isShareTokenForSameCredentials()} and
     * {@link uk.gov.hmcts.darts.cache.token.config.CacheProperties#getSharedTokenEarlyExpirationMinutes()}
     * @param applyExpiryOffset Take into account the expiry of the token
     */
    public boolean validate(TokenExpiryEnum applyExpiryOffset) {
        if (validator != null && StringUtils.isNotEmpty(tokenString)) {
            return validator.test(applyExpiryOffset, tokenString);
        }
        return StringUtils.isNotEmpty(tokenString);
    }

    public boolean validate() {
        return validate(TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY);
    }

    void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static Token readToken(String tokenStr, boolean mapToSession, TokenValidator validate) {
        Token token;

        token =  new Token(tokenStr, validate);

        setupSession(token, mapToSession);

        return token;
    }

    private static void setupSession(Token token, boolean mapToSession) {
        String sessionId;
        if (doesSessionExist()) {
            sessionId = getHttpSessionId();
        } else {
            sessionId = createTokenSession();
        }

        if (mapToSession) {
            token.setSessionId(sessionId);
        }
    }

    public static Token generateDocumentumToken(boolean mapToSession, TokenValidator validate) {
        String machineIdentifier;
        try {
            machineIdentifier = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException var10) {
            machineIdentifier = "unknown";
        }
        int seedByteCount = 20;
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        byte[] seed = secureRandom.generateSeed(seedByteCount);
        secureRandom.setSeed(seed);
        String random = String.valueOf(secureRandom.nextLong());

        // create a cookie to send back to the client
        Token token =  new Token(machineIdentifier + "-" + System.currentTimeMillis() + "-" + random + "-" + COUNTER.incrementAndGet(), validate);
        setupSession(token, mapToSession);

        return token;
    }

    private static String createTokenSession() {
        HttpServletRequest curRequest =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return curRequest.getSession().getId();
    }

    private static String getHttpSessionId() {
        HttpServletRequest curRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        return curRequest.getSession(false).getId();
    }

    private static boolean doesSessionExist() {
        HttpServletRequest curRequest =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return curRequest.getSession(false) != null;
    }
}

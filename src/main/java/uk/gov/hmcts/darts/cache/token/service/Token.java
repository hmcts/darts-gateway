package uk.gov.hmcts.darts.cache.token.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@EqualsAndHashCode
public class Token {

    private final String tokenString;

    private String sessionId = "";

    public static final AtomicLong COUNTER = new AtomicLong();

    private TokenValidator validate;

    Token(String token,  TokenValidator validate) {
        this.tokenString = token;
        this.validate = validate;
    }

    public Optional<String> getTokenString() {
        if (validate != null && !valid()) {
            return Optional.empty();
        }

        return Optional.of(tokenString);
    }

    public Optional<String> getTokenString(boolean validateTokenBefore) {
        if (validateTokenBefore && validate != null && !valid()) {
            return Optional.empty();
        }

        return Optional.of(tokenString);
    }

    @EqualsAndHashCode.Include
    public String getId() {
        return TokenRegisterable.CACHE_PREFIX + ":" + tokenString + ":" + sessionId;
    }

    public boolean valid(boolean applyExpiryOffset) {
        return valid(applyExpiryOffset, tokenString);
    }

    private boolean valid(boolean applyExpiryOffset, String token) {
        if (validate != null && !token.isEmpty()) {
            return validate.validate(applyExpiryOffset, token);
        }
        return !token.isEmpty();
    }

    public boolean valid() {
        return valid(false);
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
        if (!doesSessionExist()) {
            sessionId = createTokenSession();
        } else {
            sessionId = getHttpSessionId();
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

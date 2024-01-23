package uk.gov.hmcts.darts.cache.token.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@EqualsAndHashCode
@Getter
public class Token {

    @EqualsAndHashCode.Include
    private final String token;

    @EqualsAndHashCode.Include
    private String sessionId = "";

    public static final AtomicLong COUNTER = new AtomicLong();

    private Predicate<String> validate;

    Token(String token,  Predicate<String> validate) {
        this.token = token;
        this.validate = validate;
    }

    public Optional<String> getToken() {
        if (validate != null && !validate.test(token)) {
            return Optional.empty();
        }

        return Optional.of(token);
    }

    Optional<String> getToken(boolean validateTokenBefore) {
        if (validateTokenBefore) {
            if (validate != null && !validate.test(token)) {
                return Optional.empty();
            }
        }

        return Optional.of(token);
    }

    public String getId() {
        return token + ":" + sessionId;
    }

    public boolean valid() {
        if (validate != null && !token.isEmpty()) {
            return validate.test(token);
        }
        return !token.isEmpty();
    }

    void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static Token readToken(String tokenStr, boolean mapToSession, Predicate<String> validate) {
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

    public static Token generateDocumentumToken(boolean mapToSession, Predicate<String> validate) {
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

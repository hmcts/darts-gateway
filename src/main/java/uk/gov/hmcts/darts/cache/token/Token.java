package uk.gov.hmcts.darts.cache.token;

import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@EqualsAndHashCode
@Getter
@Setter
public class Token {

    private final String token;

    private String sessionId;

    public static final AtomicLong COUNTER = new AtomicLong();

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static Optional<Token> readToken(String tokenStr, boolean mapToSession) {
        Token token;

        if (mapToSession && doesSessionExist()) {
            token =  new Token(tokenStr);

            // create a cookie to send back to the client
            String sessionId = getHttpSessionId();
            token.setSessionId(sessionId);

            return Optional.of(token);
        } else if (!mapToSession) {
            token =  new Token(tokenStr);
            return Optional.of(token);
        }

        return Optional.empty();
    }

    public static Token generateDocumentumToken(String tokenStr, boolean mapToSession) {
        Token token =  new Token(tokenStr);

        String sessionId = createTokenSession();

        // create a cookie to send back to the client
        if (mapToSession) {
            token.setSessionId(sessionId);
        }
        return token;
    }

    public static Token generateDocumentumToken(boolean mapToSession) {
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
        Token token =  new Token(machineIdentifier + "-" + System.currentTimeMillis() + "-" + random + "-" + COUNTER.incrementAndGet());

        String sessionId = createTokenSession();

        if (mapToSession) {
            token.setSessionId(sessionId);
        }

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

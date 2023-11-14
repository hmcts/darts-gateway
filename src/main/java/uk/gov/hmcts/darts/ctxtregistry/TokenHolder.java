package uk.gov.hmcts.darts.ctxtregistry;

import jakarta.servlet.http.HttpServletRequest;
import lombok.EqualsAndHashCode;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

@EqualsAndHashCode
public class TokenHolder  {

    private final String token;

    public static final AtomicLong COUNTER = new AtomicLong();

    public TokenHolder(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static TokenHolder generateToken(String token) {
        return new TokenHolder(token);
    }

    public static TokenHolder generateToken() {
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
        createToken();

        return new TokenHolder(machineIdentifier + "-" + System.currentTimeMillis() + "-" + random + "-" + COUNTER.incrementAndGet());
    }

    private static void createToken() {
        HttpServletRequest curRequest =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        curRequest.getSession();
    }
}

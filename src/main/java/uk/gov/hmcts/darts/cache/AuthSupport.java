package uk.gov.hmcts.darts.cache;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;

@Component
@AllArgsConstructor
public class AuthSupport {

    private static final String CACHE_PREFIX = "darts-gateway-token-service";
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenValidator tokenValidator;
    private final TokenGenerator generator;


    public String getOrCreateValidToken(String username, String password) {
        return getOrCreateValidToken(username, password, false);
    }


    public String getOrCreateValidToken(String username, String password, boolean isRetry) {
        String redisKey = createRedisKey(username, password);
        try {
            String token = (String) redisTemplate.opsForValue().get(redisKey);
            if (token == null) {
                token = createToken(username, password);
            }
            tokenValidator.validateToken(token);
            return token;
        } catch (Exception e) {
            // If token validation fails, delete the token from Redis
            redisTemplate.delete(redisKey);
            if (!isRetry) {
                // Retry once to get a new token
                return getOrCreateValidToken(username, password, true);
            }
            throw e;
        }
    }

    private String createToken(String username, String password) {
        return generator.acquireNewToken(username, password);
    }

    private String createRedisKey(String username, String password) {
        return CACHE_PREFIX + ":" + username + ":" + password;
    }
}

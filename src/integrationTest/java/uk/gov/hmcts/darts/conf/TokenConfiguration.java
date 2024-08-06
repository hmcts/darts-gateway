package uk.gov.hmcts.darts.conf;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.component.impl.TokenValidatorImpl;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

import java.net.MalformedURLException;

@TestConfiguration
@Profile("token-test")
public class TokenConfiguration {

    @Bean("tokenValidatorImpl")
    public TokenValidator getValidator(SecurityProperties securityProperties, CacheProperties properties) throws Exception {
        return new TokenValidatorIgnoringExpiration(securityProperties, properties);
    }

    static class TokenValidatorIgnoringExpiration extends TokenValidatorImpl {
        public TokenValidatorIgnoringExpiration(SecurityProperties securityProperties, CacheProperties properties) throws MalformedURLException {
            super(securityProperties, properties);
        }

        public TokenValidatorIgnoringExpiration(SecurityProperties securityProperties, DefaultJWTProcessor<SecurityContext> jwtProcessor, CacheProperties cacheProperties) {
            super(securityProperties, jwtProcessor, cacheProperties);
        }

        @Override
        protected boolean shouldIgnoreValidationException(Exception e) {
            return e instanceof BadJWTException badJWTException && badJWTException.getMessage().equals("Expired JWT");
        }
    }
}

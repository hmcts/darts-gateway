package uk.gov.hmcts.darts.conf;

import com.nimbusds.jwt.proc.BadJWTException;
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
    public TokenValidator getValidator(SecurityProperties securityProperties, CacheProperties properties) throws MalformedURLException {
        return new TokenValidatorIgnoringExpiration(securityProperties, properties);
    }

    static class TokenValidatorIgnoringExpiration extends TokenValidatorImpl {
        public TokenValidatorIgnoringExpiration(SecurityProperties securityProperties, CacheProperties properties) throws MalformedURLException {
            super(securityProperties, properties);
        }

        @Override
        protected boolean shouldIgnoreValidationException(Exception exception) {
            return exception
                instanceof BadJWTException badJwtException && "Expired JWT".equals(badJwtException.getMessage());
        }
    }
}

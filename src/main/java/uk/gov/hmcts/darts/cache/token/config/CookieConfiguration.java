package uk.gov.hmcts.darts.cache.token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class CookieConfiguration {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookiePath("/");
        serializer.setSameSite(null);
        serializer.setCookieName("JSESSIONID");
        serializer.setUseHttpOnlyCookie(false);
        serializer.setUseBase64Encoding(true);
        return serializer;
    }
}
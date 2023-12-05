package uk.gov.hmcts.darts.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.darts.cache.token.DefaultCache;
import uk.gov.hmcts.darts.cache.token.JwtCache;
import uk.gov.hmcts.darts.ctxtregistry.config.ContextRegistryProperties;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class ContextRegistryCacheConfig {
    public static final String TOKEN_CACHE = "token_cache";

    public static final int ENTRY_EXPIRATION_TIME_MINUTES = 30;

    @Bean
    Cache getCache() {
        return new ConcurrentMapCache(TOKEN_CACHE, CacheBuilder.newBuilder()
            .expireAfterWrite(ENTRY_EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES)
            .build().asMap(), false);
    }

    @ConditionalOnProperty(
        value = "darts-gateway.context-registry.token-generate",
        havingValue = "documentum",
        matchIfMissing = true)
    @Bean
    DefaultCache getDefaultTokenCache(Cache cacheToUse, ContextRegistryProperties properties) {
        return new DefaultCache(cacheToUse, properties);
    }

    @ConditionalOnProperty(
        value = "darts-gateway.context-registry.token-generate",
        havingValue = "jwt",
        matchIfMissing = false)
    @Bean
    JwtCache getJwtTokenCache(Cache cacheToUse, OauthTokenGenerator jwtGenerator,
                              ContextRegistryProperties cxtProperties) {
        return new JwtCache(cacheToUse, jwtGenerator, cxtProperties);
    }

}

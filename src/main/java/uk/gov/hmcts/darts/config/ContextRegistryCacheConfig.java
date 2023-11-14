package uk.gov.hmcts.darts.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.darts.ctxtregistry.DefaultContextRegistryCache;
import uk.gov.hmcts.darts.ctxtregistry.JwtContextRegistryCache;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class ContextRegistryCacheConfig {
    public static final String TOKEN_CACHE = "token_cache";
    public static final int MAXIMUM_NUMBER_OF_ITEMS = 100;

    public static final int ENTRY_EXPIRATION_TIME_SECONDS = 10;

    @Bean
    Cache getCache() {
        return new ConcurrentMapCache(TOKEN_CACHE, CacheBuilder.newBuilder()
            .expireAfterWrite(ENTRY_EXPIRATION_TIME_SECONDS, TimeUnit.SECONDS)
                .maximumSize(MAXIMUM_NUMBER_OF_ITEMS).build().asMap(), false);
    }

    @ConditionalOnProperty(
            value = "darts-gateway.context-registry.generate-jwt",
            matchIfMissing = true)
    @Bean
    DefaultContextRegistryCache getDefaultTokenCache(Cache cacheToUse) {
        return new DefaultContextRegistryCache(cacheToUse);
    }

    @ConditionalOnProperty(
            value = "darts-gateway.context-registry.generate-jwt",
            havingValue = "jwt",
            matchIfMissing = false)
    @Bean
    JwtContextRegistryCache getJwtokenCache(Cache caheToUse, OauthTokenGenerator jwtGenerator) {
        return new JwtContextRegistryCache(caheToUse, jwtGenerator);
    }
}

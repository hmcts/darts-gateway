package uk.gov.hmcts.darts.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.internal.HostAndPort;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DnsResolvers;
import io.lettuce.core.resource.MappingSocketAddressResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.impl.TokenDocumentumIdToJwtCache;
import uk.gov.hmcts.darts.cache.token.service.impl.TokenJwtCache;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.function.UnaryOperator;

import static java.nio.charset.Charset.defaultCharset;
import static java.time.Duration.ofSeconds;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    private static final String LOCK_REGISTRY_REDIS_KEY = "MY_REDIS_KEY";
    private static final Duration RELEASE_TIME_DURATION = ofSeconds(30);

    @Value("${darts-gateway.redis.connection-string}")
    private String redisConnectionString;

    @Value("${darts-gateway.redis.ssl-enabled}")
    private boolean sslEnabled;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisConnectionProperties redisConnectionProperties = redisConnectionPropertiesFrom(redisConnectionString);
        MappingSocketAddressResolver mappingSocketAddressResolver = MappingSocketAddressResolver.create(
            DnsResolvers.JVM_DEFAULT,
            getHostAndPortMappingFunctionFor(redisConnectionProperties.host())
        );

        ClientResources clientResources = ClientResources.builder()
            .socketAddressResolver(mappingSocketAddressResolver)
            .build();

        SocketOptions socketOptions = SocketOptions.builder()
            .connectTimeout(ofSeconds(20))
            .build();

        ClientOptions clientOptions = ClientOptions.builder()
            .timeoutOptions(TimeoutOptions.enabled(ofSeconds(20)))
            .socketOptions(socketOptions)
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .build();

        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder = LettuceClientConfiguration.builder();
        clientConfigurationBuilder
            .commandTimeout(ofSeconds(20))
            .clientOptions(clientOptions)
            .clientResources(clientResources);
        if (sslEnabled) {
            clientConfigurationBuilder.useSsl();
        }

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
            redisConnectionProperties.host(),
            redisConnectionProperties.port()
        );
        redisConfig.setPassword(RedisPassword.of(redisConnectionProperties.password()));

        LettuceConnectionFactory factory =  new LettuceConnectionFactory(redisConfig, clientConfigurationBuilder.build());
        factory.setShareNativeConnection(false);
        return factory;
    }

    private UnaryOperator<HostAndPort> getHostAndPortMappingFunctionFor(String host) {
        return hostAndPort -> {
            InetAddress[] addresses = new InetAddress[0];
            try {
                addresses = DnsResolvers.JVM_DEFAULT.resolve(host);
            } catch (UnknownHostException unknownHostException) {
                log.error("Failed to resolve: " + host, unknownHostException);
            }

            String hostIp = addresses[0].getHostAddress();
            HostAndPort finalAddress = hostAndPort;
            if (hostAndPort.hostText.equals(hostIp)) {
                finalAddress = HostAndPort.of(host, hostAndPort.getPort());
            }

            return finalAddress;
        };
    }


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, RedisCacheConfiguration redisCacheConfiguration) {
        log.debug("Initializing Redis for caching...");
        return RedisCacheManager
            .builder(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory))
            .withCacheConfiguration("", cacheConfiguration())
            .cacheDefaults(redisCacheConfiguration).build();

    }

    static RedisConnectionProperties redisConnectionPropertiesFrom(String redisConnectionString) {
        RedisURI redisUri = RedisURI.create(redisConnectionString);

        RedisPassword redisPassword = RedisPassword.of(redisUri.getPassword());
        char[] decodedPasswordChars = {};
        if (redisPassword.isPresent()) {
            char[] encodedPassword = redisPassword.get();
            String decodedPasswordString = URLDecoder.decode(String.valueOf(encodedPassword), defaultCharset());
            decodedPasswordChars = decodedPasswordString.toCharArray();
        }

        return new RedisConnectionProperties(
            redisUri.getUsername(),
            decodedPasswordChars,
            redisUri.getHost(),
            redisUri.getPort());
    }

    public record RedisConnectionProperties(String username, char[] password, String host, Integer port) {

    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            //.enableTimeToIdle()
            .entryTtl(ofSeconds(10))
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    }

    @SuppressWarnings("PMD.UnnecessaryAnnotationValueElement")
    @ConditionalOnProperty(
        value = "darts-gateway.cache.token-generate",
        havingValue = "documentum-to-jwt",
        matchIfMissing = true)
    @Bean(value = "primarycache")
    TokenDocumentumIdToJwtCache getDefaultTokenCache(RedisTemplate<String, Object> template,
                                                     CacheProperties properties,
                                                     TokenGeneratable cache,
                                                     LockRegistry registry) {
        return new TokenDocumentumIdToJwtCache(template, cache, properties, registry);
    }


    @Bean
    TokenGeneratable getTokenGeneratable(RedisTemplate<String, Object> template, TokenGenerator jwtGenerator,
                                         CacheProperties cxtProperties, LockRegistry registry, TokenValidator jwtValidator) {
        return new TokenJwtCache(template, jwtGenerator, cxtProperties, registry, jwtValidator);
    }

    @SuppressWarnings("PMD.UnnecessaryAnnotationValueElement")
    @ConditionalOnProperty(
        value = "darts-gateway.cache.token-generate",
        havingValue = "jwt",
        matchIfMissing = false)
    @Bean(value = "primarycache")
    TokenJwtCache getJwtTokenCache(RedisTemplate<String, Object> template, TokenGenerator jwtGenerator,
                                   CacheProperties cxtProperties, LockRegistry registry, TokenValidator jwtValidator) {
        return new TokenJwtCache(template, jwtGenerator, cxtProperties, registry, jwtValidator);
    }

    @Bean
    public RedisTemplate<?, Object> redisTemplate(RedisConnectionFactory connectionFactory, RedisCacheConfiguration configuration) {
        RedisTemplate<?, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(redisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisLockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory) {
        RedisLockRegistry registry =  new RedisLockRegistry(redisConnectionFactory, LOCK_REGISTRY_REDIS_KEY,
                                  RELEASE_TIME_DURATION.toMillis());
        registry.setRedisLockType(RedisLockRegistry.RedisLockType.PUB_SUB_LOCK);
        return registry;
    }

    private static GenericJackson2JsonRedisSerializer redisSerializer() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        serializer.configure(objectMapper -> {
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        });
        return serializer;
    }
}

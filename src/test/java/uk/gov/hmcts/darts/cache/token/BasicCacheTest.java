package uk.gov.hmcts.darts.cache.token;

import documentum.contextreg.BasicIdentity;
import documentum.contextreg.ServiceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.service.AbstractTokenCache;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenGeneratable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;
import uk.gov.hmcts.darts.cache.token.service.value.DownstreamTokenisableValue;
import uk.gov.hmcts.darts.cache.token.service.value.impl.RefeshableTokenCacheValue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.mockito.Mockito.times;

class BasicCacheTest {

    private DummyCache cache;

    private InMemoryValueOperations redisData;

    private TokenValidator validateToken;

    private CacheProperties properties = Mockito.mock(CacheProperties.class);

    private DummyInMemoryRedisTemplate template;

    private TokenGeneratable generatable;

    private static final String TOKEN_STRING = "test";

    private static final String SESSION_ID = "testSession";

    private static final String EXISTING_SESSION_ID = "testSessionExisting";

    private static final long TOKEN_EXPIRE_SECONDS = 15L;

    private static MockedStatic<RequestContextHolder> contextHolder;

    @BeforeAll
    static void beforeAll() {
        contextHolder = Mockito.mockStatic(RequestContextHolder.class);
        ServletRequestAttributes attributes = Mockito.mock(ServletRequestAttributes.class);
        contextHolder.when(RequestContextHolder::currentRequestAttributes).thenReturn(attributes);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        HttpSession existingSession = Mockito.mock(HttpSession.class);

        Mockito.when(attributes.getRequest()).thenReturn(request);
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(existingSession);
        Mockito.when(session.getId()).thenReturn(SESSION_ID);
        Mockito.when(existingSession.getId()).thenReturn(EXISTING_SESSION_ID);
    }

    @AfterAll
    public static void close() {
        contextHolder.close();
    }

    @BeforeEach
    void before() {
        redisData = new InMemoryValueOperations();

        template = new DummyInMemoryRedisTemplate(redisData);

        properties = Mockito.mock(CacheProperties.class);

        LockRegistry registry = Mockito.mock(LockRegistry.class);

        Lock lock = Mockito.mock(Lock.class);
        Mockito.when(registry.obtain(Mockito.notNull())).thenReturn(lock);

        validateToken = Mockito.mock(TokenValidator.class);
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);

        generatable = Mockito.mock(TokenGeneratable.class);

        cache = new DummyCache(template, properties, registry, validateToken, TOKEN_STRING, generatable);

        Mockito.when(properties.isShareTokenForSameCredentials()).thenReturn(false);
        Mockito.when(properties.getEntryTimeToIdleSeconds()).thenReturn(TOKEN_EXPIRE_SECONDS);
    }

    @Test
    void testRegisterCreateNew() {
        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";
        TokenValidator validateToken = Mockito.mock(TokenValidator.class);
        Token returnToken = Token.readToken(valueToken, false, validateToken);
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Mockito.when(generatable.createToken(Mockito.eq(context))).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);
        Assertions.assertEquals(TOKEN_STRING, token.get().getTokenString());
        Assertions.assertEquals(value, redisData.getModel().get(token.get().getKey()));
        Assertions.assertNotNull(value.getServiceContext());
        Assertions.assertEquals(valueToken, ((DownstreamTokenisableValue) value).getToken().getTokenString());
    }

    @Test
    void testRegisterWithExisting() {
        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";
        TokenValidator valueValidateToken = (expireBefore, t) -> true;
        Token returnToken = Token.readToken(valueToken, false, valueValidateToken);
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Mockito.when(generatable.createToken(Mockito.eq(context))).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);
        Assertions.assertEquals(TOKEN_STRING, token.get().getTokenString());
        Assertions.assertEquals(value, redisData.getModel().get(token.get().getKey()));

        Mockito.when(properties.isShareTokenForSameCredentials()).thenReturn(true);
        Optional<Token> existingToken = cache.store(value);

        Assertions.assertNotNull(template.getExpireDuration().get(value.getSharedKey()));
        Assertions.assertNotNull(template.getExpireDuration().get(existingToken.get().getKey()));
        Assertions.assertEquals(existingToken.get().getKey(), token.get().getKey());
    }

    @Test
    void testLookup() {
        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";
        TokenValidator validateToken = Mockito.mock(TokenValidator.class);
        Token returnToken = Token.readToken(valueToken, false, validateToken);
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);
        Mockito.when(generatable.createToken(Mockito.notNull())).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);

        Optional<CacheValue> refreshableCacheValue = cache.lookup(token.get());

        Assertions.assertEquals(value.getContextString(), refreshableCacheValue.get().getContextString());
        Assertions.assertEquals(value, redisData.getModel().get(token.get().getKey()));
        Assertions.assertEquals(TOKEN_EXPIRE_SECONDS, template.getExpireDuration().get(token.get().getKey()).getSeconds());
        Assertions.assertEquals(valueToken, ((DownstreamTokenisableValue) value).getToken().getTokenString());
    }

    @Test
    void testLookupWithEvictionDueToInvalidToken() {
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(false);

        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";
        TokenValidator validateToken = Mockito.mock(TokenValidator.class);
        Token returnToken = Token.readToken(valueToken, false, validateToken);
        Mockito.when(generatable.createToken(Mockito.eq(context))).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);

        Optional<CacheValue> refreshableCacheValue = cache.lookup(token.get());

        Assertions.assertTrue(refreshableCacheValue.isEmpty());
    }

    @Test
    void testLookupWithValueRefresh() {
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);

        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";

        // the token after refresh
        String valueTokenAlternative = "THIS IS MY VALUE TOKEN ALTERNATIVE";

        TokenValidator valueValidateToken = (expire, t) -> true;
        TokenValidator valueValidateTokenFalse = (expire, t) -> false;

        Token returnToken = Token.readToken(valueToken, false, valueValidateToken);
        Optional<Token> returnTokenAlternative = Optional.of(Token.readToken(valueTokenAlternative, false, valueValidateTokenFalse));

        Mockito.when(generatable.createToken(Mockito.notNull())).thenReturn(returnToken).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnTokenAlternative.get()).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);

        Optional<CacheValue> refreshableCacheValue = cache.lookup(token.get());

        Assertions.assertFalse(refreshableCacheValue.isEmpty());

        DummyRefreshableCacheValueWithJwt dummyRefreshableCacheValueWithJwt = (DummyRefreshableCacheValueWithJwt) refreshableCacheValue.get();
        Assertions.assertEquals(valueToken, dummyRefreshableCacheValueWithJwt.getToken().getTokenString());
        Assertions.assertTrue(dummyRefreshableCacheValueWithJwt.hasRefreshed);
        Assertions.assertEquals(value.getContextString(), refreshableCacheValue.get().getContextString());
        Assertions.assertEquals(TOKEN_EXPIRE_SECONDS, template.getExpireDuration().get(token.get().getKey()).getSeconds());

        Mockito.verify(generatable, times(2)).createToken(Mockito.notNull());
    }

    @Test
    void testEvict() {
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);

        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";

        Token returnToken = Token.readToken(valueToken, false, validateToken);

        Mockito.when(generatable.createToken(Mockito.eq(context))).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);

        cache.evict(token.get());

        Assertions.assertNull(redisData.getModel().get(token.get().getKey()));
    }

    @Test
    void testNoEvictOfSharedToken() {
        Mockito.when(validateToken.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.notNull())).thenReturn(true);

        ServiceContext context = new ServiceContext();
        BasicIdentity basicIdentity = new BasicIdentity();
        basicIdentity.setUserName("username");
        basicIdentity.setPassword("password");
        context.getIdentities().add(basicIdentity);

        String valueToken = "THIS IS MY VALUE TOKEN";

        TokenValidator validateToken = Mockito.mock(TokenValidator.class);
        Token returnToken = Token.readToken(valueToken, false, validateToken);

        // put the test into share mode
        Mockito.when(generatable.createToken(Mockito.eq(context))).thenReturn(returnToken);
        Mockito.when(generatable.getToken(Mockito.notNull())).thenReturn(returnToken);

        CacheValue value = cache.createValue(context);
        Optional<Token> token = cache.store(value);

        Mockito.when(properties.isShareTokenForSameCredentials()).thenReturn(true);

        cache.evict(token.get());

        Assertions.assertNotNull(redisData.getModel().get(token.get().getKey()));
    }

    static class DummyInMemoryRedisTemplate extends RedisTemplate<String, Object> {
        private final InMemoryValueOperations values;

        private final Map<String, Duration> map = new HashMap<>();

        public DummyInMemoryRedisTemplate(InMemoryValueOperations values) {
            super();
            this.values = values;
        }

        @Override
        public Boolean expire(String key, Duration timeout) {
            map.put(key, timeout);
            return true;
        }

        @Override
        public Boolean delete(String key) {
            values.getModel().remove(key);
            return true;
        }

        @Override
        public ValueOperations<String, Object> opsForValue() {
            return values;
        }

        public Map<String, Duration> getExpireDuration() {
            return map;
        }
    }


    class DummyCache extends AbstractTokenCache implements TokenGeneratable {
        private final TokenValidator validate;

        private final String token;

        private final TokenGeneratable generatable;

        public DummyCache(RedisTemplate<String, Object> template, CacheProperties properties,
                          LockRegistry registry, TokenValidator validate, String token, TokenGeneratable generatable) {
            super(template, registry, properties);
            this.validate = validate;
            this.token = token;
            this.generatable = generatable;
        }

        @Override
        protected TokenValidator getTokenValidator() {
            return validate;
        }

        @Override
        public Token createToken(ServiceContext context) {
            return Token.readToken(token, false, getTokenValidator());
        }

        @Override
        public RefeshableTokenCacheValue createValue(ServiceContext context) {
            return new DummyRefreshableCacheValueWithJwt(context, generatable);
        }

        @Override
        protected CacheValue getValue(CacheValue holder) {
            return holder;
        }

        @Override
        public Token getToken(String token) {
            return Token.readToken(token, properties.isMapTokenToSession(), validate);
        }

        @Override
        public String getIdForServiceContext(ServiceContext serviceContext) {
            return RefeshableTokenCacheValue.getId(serviceContext);
        }
    }

    class DummyRefreshableCacheValueWithJwt extends RefeshableTokenCacheValue {
        boolean hasRefreshed;

        public DummyRefreshableCacheValueWithJwt(ServiceContext context, TokenGeneratable registerable) {
            super(context, registerable);
        }

        public DummyRefreshableCacheValueWithJwt(DummyRefreshableCacheValueWithJwt context, TokenGeneratable registerable) {
            super(context, registerable);
        }

        @Override
        public void performRefresh() {
            hasRefreshed = true;
            super.performRefresh();
        }
    }
}

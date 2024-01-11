package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClientProvider;

@ActiveProfiles("int-test-documentum-jwt-token-shared")
class ContextRegistryDocumentumToJwtServiceSharedTokenTest extends ContextRegistryParent {
    @MockBean
    private OauthTokenGenerator generator;

    @Autowired
    private CacheProperties properties;

    @Autowired
    private RedisTemplate<String, Object> restTemplate;

    private static final int REGISTERED_COUNT = 10;

    @BeforeEach
    public void before() {
        Mockito.when(generator.acquireNewToken("dmadmin", "dmadmin")).thenReturn("test");
        for (int i = 0; i < REGISTERED_COUNT; i++) {
            Mockito.when(generator.acquireNewToken("user" + i, "pass")).thenReturn("test2");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleRegister(ContextRegistryClient client) throws Exception {
        executeHandleRegister(client);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleRegisterWithSharing(ContextRegistryClient client) throws Exception {
        executeHandleRegisterWithSharing(client);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleLookup(ContextRegistryClient client) throws Exception {
        executeHandleLookup(client);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToLive(ContextRegistryClient client) throws Exception {
        executeTestTimeToLive(client, properties);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToIdle(ContextRegistryClient client) throws Exception {
        executeTestTimeToIdle(client, properties);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testBasicConcurrency(ContextRegistryClient client) throws Exception {
        executeBasicConcurrency(client, REGISTERED_COUNT, properties);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleUnregisterSharing(ContextRegistryClient client) throws Exception {
        executeTestHandleUnregisterSharing(client);
    }
}

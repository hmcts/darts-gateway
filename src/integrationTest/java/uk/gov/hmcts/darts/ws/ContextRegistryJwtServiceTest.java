package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.config.OauthTokenGenerator;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClientProvider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token")
public class ContextRegistryJwtServiceTest extends ContextRegistryParent{
    @MockBean
    private OauthTokenGenerator generator;

    @Autowired
    private CacheProperties properties;

    private static final int REGISTERED_USER_COUNT = 10;

    @BeforeEach
    public void before() {
        when(generator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("test");

        for (int i = 0; i < REGISTERED_USER_COUNT; i++) {
            Mockito.when(generator.acquireNewToken("user" + i, "pass")).thenReturn("test2");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () ->
        {
            executeHandleRegister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () ->
        {
            executeHandleRegister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesRegisterWithAuthenticatiooTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleRegister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }


    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegister(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleRegister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegisterWithAuthenticationToken(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeHandleRegister(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testLookupWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () ->
        {
            executeHandleLookup(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testLookupWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () ->
        {
            executeHandleLookup(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesLookupWithAuthenticationTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleLookup(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleLookup(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleLookup(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleLookupWithAuthenticationToken(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeHandleLookup(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToLive(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestTimeToLive(client, properties);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToIdle(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestTimeToIdle(client, properties);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testBasicConcurrency(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeBasicConcurrency(client, REGISTERED_USER_COUNT, properties);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testUnregisterWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () ->
        {
            executeTestHandleUnregister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testUnregisterWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () ->
        {
            executeTestHandleUnregister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesUnregisterWithAuthenticationTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeTestHandleUnregister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testGetContextRegistry() throws Exception {
        executeTestGetContextRegistryWsdl();
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHhandleUnregister(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestHandleUnregister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleUnregisterWithToken(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeTestHandleUnregister(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}

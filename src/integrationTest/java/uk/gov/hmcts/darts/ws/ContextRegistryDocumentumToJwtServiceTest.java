package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-documentum-jwt-token")
class ContextRegistryDocumentumToJwtServiceTest extends ContextRegistryParent {
    @MockitoBean
    private TokenGenerator generator;

    @Autowired
    private CacheProperties properties;

    @MockitoBean
    private TokenValidator tokenValidator;

    private static final int REGISTERED_USER_COUNT = 10;

    private static final String CONTEXT_REGISTRY_TOKEN = "testToken";

    @BeforeEach
    public void before() {
        when(generator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn("test");

        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test"))).thenReturn(true);

        when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
            .thenReturn(CONTEXT_REGISTRY_TOKEN);

        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);
        when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);

        for (int i = 0; i < REGISTERED_USER_COUNT; i++) {

            when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test2"))).thenReturn(true);
            when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test2"))).thenReturn(true);

            when(generator.acquireNewToken(Mockito.eq("user" + i), Mockito.eq("pass"))).thenReturn("test2");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_REGISTER_USERNAME, DEFAULT_REGISTER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeHandleRegister(client);
        }, DEFAULT_REGISTER_USERNAME, DEFAULT_REGISTER_PASSWORD);

        verify(generator,times(1)).acquireNewToken(DEFAULT_REGISTER_USERNAME, DEFAULT_REGISTER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeHandleRegisterMissingIdentity(client);
        });

        verify(generator, times(0)).acquireNewToken(anyString(), anyString());
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesRegisterWithAuthenticationTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleRegister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegister(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleRegister(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleRegisterWithAuthenticationToken(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeHandleRegister(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterFailsIfNoServiceContextInRegisterBody(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailsWithRegisterNullServiceContextException(client, () -> {
            executeHandleRegisterMissingServiceContext(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testLookupWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeHandleLookup(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(generator, times(2)).acquireNewToken(DEFAULT_REGISTER_USERNAME, DEFAULT_REGISTER_PASSWORD);
        verify(generator, times(1)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testLookupWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeHandleLookup(client);
        });

        verify(generator, times(2)).acquireNewToken(DEFAULT_REGISTER_USERNAME, DEFAULT_REGISTER_PASSWORD);
        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesLookupWithAuthenticationTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleLookup(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleLookup(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleLookup(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleLookupWithAuthenticationToken(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeHandleLookup(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToLive(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestTimeToLive(client, properties);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToIdle(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestTimeToIdle(client, properties);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testBasicConcurrency(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeBasicConcurrency(client, REGISTERED_USER_COUNT, properties);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testUnregisterWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeTestHandleUnregister(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testUnregisterWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeTestHandleUnregister(client);
        });

        verify(generator, times(2)).acquireNewToken(DEFAULT_REGISTER_USERNAME, DEFAULT_REGISTER_PASSWORD);
        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesUnregisterWithAuthenticationTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeTestHandleUnregister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testGetContextRegistry() throws Exception {
        executeTestGetContextRegistryWsdl();
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleUnregister(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestHandleUnregister(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleUnregisterWithToken(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeTestHandleUnregister(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

}

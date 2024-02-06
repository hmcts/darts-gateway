package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.component.TokenValidator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClientProvider;

import java.net.URL;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ActiveProfiles("int-test-jwt-token-shared")
class ContextRegistryJwtServiceSharedTokenTest extends ContextRegistryParent {

    @MockBean
    private TokenGenerator generator;

    @Autowired
    private CacheProperties properties;

    @MockBean
    private TokenValidator tokenValidator;

    private static final int REGISTERED_USER_COUNT = 10;

    private static final String CONTEXT_REGISTRY_TOKEN = "testToken";

    @BeforeEach
    public void before() {
        when(generator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenReturn("test");

        when(tokenValidator.validate(Mockito.eq(true), Mockito.eq("test"))).thenReturn(true);
        when(tokenValidator.validate(Mockito.eq(false), Mockito.eq("test"))).thenReturn(true);

        when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
            .thenReturn(CONTEXT_REGISTRY_TOKEN);

        when(tokenValidator.validate(Mockito.eq(true), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);
        when(tokenValidator.validate(Mockito.eq(false), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);

        for (int i = 0; i < REGISTERED_USER_COUNT; i++) {

            when(tokenValidator.validate(Mockito.eq(true), Mockito.eq("test2"))).thenReturn(true);
            when(tokenValidator.validate(Mockito.eq(false), Mockito.eq("test2"))).thenReturn(true);

            when(generator.acquireNewToken(Mockito.eq("user" + i), Mockito.eq("pass"))).thenReturn("test2");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithAuthenticationFailure(ContextRegistryClient client) throws Exception {

        when(generator.acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeHandleRegister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeHandleRegister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesRegisterWithAuthenticationTokenFailure(ContextRegistryClient client) throws Exception {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleRegister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testhandleRegister(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleRegister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegisterExpiry(ContextRegistryClient client) throws Exception {
        when(tokenValidator.validate(Mockito.anyBoolean(), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);

        String refreshedToken = "refreshToken";
        when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
            .thenReturn(CONTEXT_REGISTRY_TOKEN, CONTEXT_REGISTRY_TOKEN, refreshedToken, refreshedToken);

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String token = registerToken(client);

            String token2 = registerToken(client);

            // ensure we are returning the same token string
            Assertions.assertEquals(token, token2);

            when(tokenValidator.validate(Mockito.anyBoolean(), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(false);
            when(tokenValidator.validate(Mockito.anyBoolean(), Mockito.eq(refreshedToken))).thenReturn(true);

            token2 = registerToken(client);

            when(tokenValidator.validate(Mockito.anyBoolean(), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);

            executeHandleLookupForToken(client, token);

            Assertions.assertNotEquals(token, token2);
            Assertions.assertEquals(refreshedToken, token2);

            verify(tokenValidator, times(12)).validate(Mockito.anyBoolean(), Mockito.eq(CONTEXT_REGISTRY_TOKEN));
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

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeHandleLookup(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testLookupWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeHandleLookup(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleLookupTokenExpired(ContextRegistryClient client) throws Exception {
        when(tokenValidator.validate(Mockito.anyBoolean(), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true, true, true, false);

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {

            String token = registerToken(client);

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
            soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

            SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
            Assertions.assertNull(response.getResponse().getValue().getReturn());
            verify(tokenValidator, times(4)).validate(Mockito.anyBoolean(), Mockito.eq(CONTEXT_REGISTRY_TOKEN));
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
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
    void testHandleLookupWithAuthenticationToken(ContextRegistryClient client) throws Exception {
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

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeTestHandleUnregister(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);

        verify(generator).acquireNewToken(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTestUnregisterWithNoIdentities(ContextRegistryClient client) throws Exception {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeTestHandleUnregister(client);
        });

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
    void testhandleRegisterWithSharing(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleRegisterWithSharing(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleUnregisterSharing(ContextRegistryClient client) throws Exception {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestHandleUnregisterSharing(client);
        }, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}

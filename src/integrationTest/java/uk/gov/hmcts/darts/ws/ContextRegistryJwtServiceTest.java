package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.darts.cache.AuthSupport;
import uk.gov.hmcts.darts.cache.token.component.TokenGenerator;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.transform.TransformerException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("int-test-jwt-token")
@Slf4j
class ContextRegistryJwtServiceTest extends ContextRegistryParent {
    @MockitoBean
    private TokenGenerator generator;

    @Autowired
    private CacheProperties properties;

    @MockitoBean
    private AuthSupport authSupport;

    private static final int REGISTERED_USER_COUNT = 10;

    private static final String CONTEXT_REGISTRY_TOKEN = "contextRegistryToken";
    private static final String HEADER_TOKEN = "headerToken";

    @BeforeEach
    public void before() {
        when(generator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenReturn(HEADER_TOKEN);

        //when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq(HEADER_TOKEN))).thenReturn(true);
        //when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq(HEADER_TOKEN))).thenReturn(true);

        when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
            .thenReturn(CONTEXT_REGISTRY_TOKEN);

        //when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);
        //when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);

        for (int i = 0; i < REGISTERED_USER_COUNT; i++) {

            //    when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.DO_NOT_APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test2"))).thenReturn(true);
            //    when(tokenValidator.test(Mockito.eq(Token.TokenExpiryEnum.APPLY_EARLY_TOKEN_EXPIRY), Mockito.eq("test2"))).thenReturn(true);

            when(generator.acquireNewToken(Mockito.eq("user" + i), Mockito.eq("pass"))).thenReturn("test2");
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithAuthenticationFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeHandleRegister(client);
        }, SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);

        verify(generator).acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRegisterWithNoIdentities(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeHandleRegisterMissingIdentity(client);
        });

        verify(generator, times(0)).acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }


    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)

    void testRegisterWithInvalidIdentities(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        authenticationStub.assertFailBasedOnInvalidIdentities(client, () -> {
            executeHandleRegisterInvalidIdentity(client);
        });

        verify(generator, times(0)).acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesRegisterWithAuthenticationTokenFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleRegister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }


    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegister(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleRegister(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegisterFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {
        //when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
        //    .thenThrow(new CacheTokenCreationException(""));
        authenticationStub.assertWithNoHeaderInvalidCredentials(() -> {
            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/soapRequest.xml");

            soapRequestStr = soapRequestStr.replace("${USER}", SERVICE_CONTEXT_USER);
            soapRequestStr = soapRequestStr.replace("${PASSWORD}", SERVICE_CONTEXT_PASSWORD);

            SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
            Assertions.assertNull(response.getResponse().getValue().getReturn());
        });
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleRegisterWithAuthenticationToken(ContextRegistryClient client) throws IOException, JAXBException, InterruptedException {
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
    void testLookupWithAuthenticationFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        when(generator.acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeHandleLookup(client);
        }, SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);

        verify(generator).acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testLookupWithNoIdentities(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeHandleLookupNoIdentities(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesLookupWithAuthenticationTokenFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeHandleLookup(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleLookup(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeHandleLookup(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleLookupTokenExpired(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        //when(tokenValidator.test(Mockito.any(), Mockito.eq(CONTEXT_REGISTRY_TOKEN))).thenReturn(true);
        //when(tokenValidator.test(Mockito.any(), Mockito.eq(HEADER_TOKEN))).thenReturn(false, true);

        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            String token = registerToken(client);

            String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
            soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

            SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
            Assertions.assertNotNull(response.getResponse().getValue().getReturn());

            //    verify(tokenValidator, times(4)).test(Mockito.any(), Mockito.eq(CONTEXT_REGISTRY_TOKEN));
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleLookupWithAuthenticationToken(ContextRegistryClient client) throws IOException, JAXBException, InterruptedException {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeHandleLookup(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToLive(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestTimeToLive(client, properties);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testTimeToIdle(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestTimeToIdle(client, properties);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testBasicConcurrency(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeBasicConcurrency(client, REGISTERED_USER_COUNT, properties);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testUnregisterWithAuthenticationFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        when(generator.acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD))
            .thenThrow(new RuntimeException());

        authenticationStub.assertFailBasedOnNotAuthenticatedForUsernameAndPassword(client, () -> {
            executeTestHandleUnregister(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);

        verify(generator, times(4)).acquireNewToken(SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
        verify(generator).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testUnregisterWithNoIdentities(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {

        authenticationStub.assertFailBasedOnNoIdentities(client, () -> {
            executeTestHandleUnregister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        //todo add back in? verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testRoutesUnregisterWithAuthenticationTokenFailure(ContextRegistryClient client) throws IOException, TransformerException, InterruptedException {
        authenticationStub.assertFailBasedOnNotAuthenticatedToken(client, () -> {
            executeTestHandleUnregister(client);
        });

        verify(generator, times(0)).acquireNewToken(DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
        verifyNoMoreInteractions(generator);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testGetContextRegistry() throws IOException, InterruptedException, URISyntaxException {
        executeTestGetContextRegistryWsdl();
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHhandleUnregister(ContextRegistryClient client) throws JAXBException, IOException, InterruptedException {
        authenticationStub.assertWithUserNameAndPasswordHeader(client, () -> {
            executeTestHandleUnregister(client);
        }, DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void testHandleUnregisterWithToken(ContextRegistryClient client) throws IOException, JAXBException, InterruptedException {
        authenticationStub.assertWithTokenHeader(client, () -> {
            executeTestHandleUnregister(client);
        }, getContextClient(), getGatewayUri(), DEFAULT_HEADER_USERNAME, DEFAULT_HEADER_PASSWORD);
    }
}

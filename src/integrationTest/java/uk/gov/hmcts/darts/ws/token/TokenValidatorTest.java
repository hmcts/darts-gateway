package uk.gov.hmcts.darts.ws.token;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.common.exceptions.soap.FaultErrorCodes;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.testutils.DartsTokenAndJwksKey;
import uk.gov.hmcts.darts.testutils.DartsTokenGenerator;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.testutils.request.ContextRequestHelper;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"int-test"})
class TokenValidatorTest extends IntegrationBase {

    @Autowired
    private ContextRequestHelper contextRequestHelper;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("logging.level.root", () -> "DEBUG");
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkTokenExpiry(ContextRegistryClient client) throws Exception {
        DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri()).useExpiredToken(true)
            .audience(securityProperties.getClientId()).build();
        DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

        tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
        tokenStub.stubExternalToken(tokenDetails.getToken());

        SoapFaultClientException exception = assertThrows(SoapFaultClientException.class,
                                                          () -> contextRequestHelper.registerToken(client,
                                                                                                   getGatewayUri()).getResponse().getValue().getReturn());
        assertEquals("Authorization failed, please review the identities provided", exception.getMessage());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkInvalidAudience(ContextRegistryClient client) throws Exception {
        DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri()).useExpiredToken(true)
            .audience("invalidAudience").build();
        DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

        tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
        tokenStub.stubExternalToken(tokenDetails.getToken());

        SoapFaultClientException exception = assertThrows(SoapFaultClientException.class,
                                                          () -> contextRequestHelper.registerToken(client,
                                                                                                   getGatewayUri()).getResponse().getValue().getReturn());
        assertEquals("Authorization failed, please review the identities provided", exception.getMessage());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkInvalidIssuer(ContextRegistryClient client) throws Exception {
        DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer("invalidIssuer").useExpiredToken(true)
            .audience(securityProperties.getClientId()).build();
        DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

        tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
        tokenStub.stubExternalToken(tokenDetails.getToken());

        SoapFaultClientException exception = assertThrows(SoapFaultClientException.class,
                                                          () -> contextRequestHelper.registerToken(client,
                                                                                                   getGatewayUri()).getResponse().getValue().getReturn());
        assertEquals("Authorization failed, please review the identities provided", exception.getMessage());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkInvalidTokenSignature(ContextRegistryClient client) throws Exception {
        DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri()).useExpiredToken(true)
            .audience("invalidAudience").build();
        DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();
        tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());

        tokenDetails = generatedToken.fetchTokenWithGlobalUser();
        tokenStub.stubExternalToken(tokenDetails.getToken());


        SoapFaultClientException exception = assertThrows(SoapFaultClientException.class,
                                                          () -> contextRequestHelper.registerToken(client,
                                                                                                   getGatewayUri()).getResponse().getValue().getReturn());
        assertEquals("Authorization failed, please review the identities provided", exception.getMessage());
    }
}

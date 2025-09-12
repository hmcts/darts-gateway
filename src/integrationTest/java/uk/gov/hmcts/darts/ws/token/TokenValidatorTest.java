package uk.gov.hmcts.darts.ws.token;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.testutils.DartsTokenAndJwksKey;
import uk.gov.hmcts.darts.testutils.DartsTokenGenerator;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.testutils.request.ContextRequestHelper;

import java.net.URL;

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
    void checkRefreshOfPublicKeys(ContextRegistryClient client) throws Exception {
        String token = runOperationExpectingJwksRefresh(null,  (t)
            -> {

            DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri())
                .audience(securityProperties.getClientId()).build();
            DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

            tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
            tokenStub.stubExternalToken(tokenDetails.getToken());

            return contextRequestHelper.registerToken(client, getGatewayUri()).getResponse().getValue().getReturn();
        });

        runOperationExpectingJwksRefresh(token, (t) -> {
            String lookupRequest = TestUtils.getContentsFromFile(
                                              "payloads/ctxtRegistry/lookup/soapRequest.xml");

            lookupRequest = lookupRequest.replace("${TOKEN}", t);
            client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), lookupRequest);
            return null;
        });

        runOperationExpectingJwksRefresh(token, (t) -> {
            String lookupRequest = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
            lookupRequest = lookupRequest.replace("${TOKEN}", t);
            client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), lookupRequest);
            return null;
        });

        // a total of 3 public key fetches should be seen based on the configuration properties
        tokenStub.verifyNumberOfTimesKeysObtained(3);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkTokenExpiry(ContextRegistryClient client) throws Exception {
        runOperationExpectingJwksRefresh(null, (t) -> {
            DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri()).useExpiredToken(true)
                .audience(securityProperties.getClientId()).build();
            DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

            tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
            tokenStub.stubExternalToken(tokenDetails.getToken());

            return contextRequestHelper.registerToken(client, getGatewayUri()).getResponse().getValue().getReturn();
        }
        );

        Assertions.assertFalse(logAppender.searchLogs("JWT Token is expired", "Expired JWT", Level.WARN).isEmpty());
        Assertions.assertFalse(logAppender.searchLogs("JWT Token is expired", "not found in registry central", Level.WARN).isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkInvalidAudience(ContextRegistryClient client) throws Exception {
        runOperationExpectingJwksRefresh(null, (t) -> {
            DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri()).useExpiredToken(true)
                .audience("invalidAudience").build();
            DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

            tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
            tokenStub.stubExternalToken(tokenDetails.getToken());

            return contextRequestHelper.registerToken(client, getGatewayUri()).getResponse().getValue().getReturn();
            }
        );

        Assertions.assertFalse(logAppender.searchLogs("JWT Token could not be validated", "JWT audience rejected", Level.ERROR).isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkInvalidIssuer(ContextRegistryClient client) throws Exception {
        runOperationExpectingJwksRefresh(null, (t) -> {
            DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer("invalidIssuer").useExpiredToken(true)
                .audience(securityProperties.getClientId()).build();
            DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();

            tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());
            tokenStub.stubExternalToken(tokenDetails.getToken());

            return contextRequestHelper.registerToken(client, getGatewayUri()).getResponse().getValue().getReturn();
            }
        );

        Assertions.assertFalse(logAppender.searchLogs(
            "JWT Token could not be validated", "JWT iss claim has value invalidIssuer, must be test-issuer", Level.ERROR).isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkInvalidTokenSignature(ContextRegistryClient client) throws Exception {
        runOperationExpectingJwksRefresh(null, (t) -> {
            DartsTokenGenerator generatedToken = DartsTokenGenerator.builder().issuer(securityProperties.getIssuerUri()).useExpiredToken(true)
                .audience("invalidAudience").build();
            DartsTokenAndJwksKey tokenDetails = generatedToken.fetchTokenWithGlobalUser();
            tokenStub.stubExternalJwksKeys(tokenDetails.getJwksKey());

            tokenDetails = generatedToken.fetchTokenWithGlobalUser();
            tokenStub.stubExternalToken(tokenDetails.getToken());

            return contextRequestHelper.registerToken(client, getGatewayUri()).getResponse().getValue().getReturn();
            }
        );

        Assertions.assertFalse(logAppender.searchLogs(
            "JWT Token could not be validated", "Signed JWT rejected: Invalid signature", Level.ERROR).isEmpty());
    }
}

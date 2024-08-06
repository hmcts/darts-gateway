package uk.gov.hmcts.darts.ws.token;

import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.testutils.request.ContextRequestHelper;
import uk.gov.hmcts.darts.testutils.stub.TokenStub;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ActiveProfiles({"int-test", "test-token"})
class TokenJwksTest extends IntegrationBase {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ContextRequestHelper contextRequestHelper;

    protected TokenStub tokenStub = new TokenStub();

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkRefreshOfPublicKeys(ContextRegistryClient client) throws Exception {
        // make sure we leave enough time for the refresh between runs
        String token = runOperationExpectingJwksRefresh(null,  (t)
            -> contextRequestHelper.registerToken(client, getGatewayUri()).getResponse().getValue().getReturn());

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

    @SuppressWarnings("PMD.DoNotUseThreads")
    private String runOperationExpectingJwksRefresh(String token, JkwsRefreshableRunnable runnable) throws InterruptedException, IOException, JAXBException {
        // make sure we have left it enough time for the refresh to place
        Thread.sleep(securityProperties.getJwksCacheRefreshPeriod().toMillis() + Duration.of(1, ChronoUnit.SECONDS).toMillis());
        return runnable.run(token);
    }

    @FunctionalInterface
    interface JkwsRefreshableRunnable {
        String run(String token) throws InterruptedException, IOException, JAXBException;
    }
}

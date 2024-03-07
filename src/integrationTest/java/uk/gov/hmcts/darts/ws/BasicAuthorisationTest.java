package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.darts.DartsClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

@ActiveProfiles("int-test-jwt-token-shared")
class BasicAuthorisationTest extends IntegrationBase {

    @ParameterizedTest
    @ArgumentsSource(DartsClientProvider.class)
    void testBasicAuthorisationRequestFromNotWhitelistedServiceFails(DartsGatewayClient client) throws Exception {

        authenticationStub.assertFailBasedOnBasicAuthorisationError(client, () -> {
            String soapRequestStr = TestUtils.getContentsFromFile("payloads/getCases/soapRequest.xml");

            client.getCases(getGatewayUri(), soapRequestStr);
        }, "not_whitelisted_service", DEFAULT_PASSWORD);
    }

}

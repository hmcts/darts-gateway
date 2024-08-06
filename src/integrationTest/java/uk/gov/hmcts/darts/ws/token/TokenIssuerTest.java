package uk.gov.hmcts.darts.ws.token;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import documentum.contextreg.RegisterResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.testutils.LogUtil;
import uk.gov.hmcts.darts.testutils.request.ContextRequestHelper;
import uk.gov.hmcts.darts.testutils.stub.TokenStub;

import java.net.URL;

public class TokenIssuerTest extends IntegrationBase {

    @Autowired
    private SecurityProperties securityProperties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {}

    protected TokenStub tokenStub = new TokenStub();

    @Autowired
    private ContextRequestHelper contextRequestHelper;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("darts-gateway.security.issuer-uri", () -> {
            return "unknown issuer";
        });
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    public void checkTokenInvalidDueToIssuer(ContextRegistryClient client) throws Exception {
        contextRequestHelper.registerToken(client, getGatewayUri());

        Assertions.assertTrue(!LogUtil.getMemoryLogger().searchLogs("JWT Token could not be validated", "JWT iss claim has value", Level.ERROR).isEmpty());
    }

}

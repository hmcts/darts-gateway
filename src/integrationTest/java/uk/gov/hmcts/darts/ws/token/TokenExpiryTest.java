package uk.gov.hmcts.darts.ws.token;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.testutils.LogUtil;
import uk.gov.hmcts.darts.testutils.request.ContextRequestHelper;

class TokenExpiryTest extends IntegrationBase {

    @Autowired
    private ContextRequestHelper contextRequestHelper;

    @Autowired
    private SecurityProperties securityProperties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {}

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void checkTokenInvalidDueToExpiry(ContextRegistryClient client) throws Exception {
        // make sure we leave enough time for the refresh between runs
        contextRequestHelper.registerToken(client, getGatewayUri());

        Assertions.assertFalse(LogUtil.getMemoryLogger().searchLogs("JWT Token could not be validated", "Expired JWT", Level.ERROR).isEmpty());
    }
}

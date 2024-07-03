package uk.gov.hmcts.darts;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.common.AccessTokenClient;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.common.client.FunctionalTestClient;
import uk.gov.hmcts.darts.common.configuration.AuthConfiguration;
import uk.gov.hmcts.darts.common.configuration.ContextClientConfiguration;
import uk.gov.hmcts.darts.common.configuration.MtomClientConfig;
import uk.gov.hmcts.darts.properties.AzureAdB2CAuthenticationProperties;
import uk.gov.hmcts.darts.properties.FunctionalProperties;

import java.io.IOException;
import java.net.URI;

@SpringBootTest(classes = {AuthConfiguration.class, ContextClientConfiguration.class,
    AzureAdB2CAuthenticationProperties.class, FunctionalProperties.class, MtomClientConfig.class})
@ActiveProfiles({"functionalTest"})
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class FunctionalTest {

    protected static final String GATEWAY_WEB_CONTEXT = "/service/darts";

    @Autowired
    @Qualifier("viqClient")
    protected ContextRegistryClientWrapper viq;

    @Autowired
    @Qualifier("xhibitClient")
    protected ContextRegistryClientWrapper xhibit;

    @Autowired
    @Qualifier("cppClient")
    protected ContextRegistryClientWrapper cpp;

    @Autowired
    private AccessTokenClient client;

    @Value("${darts-gateway.deployed-application-uri}")
    private URI baseUri;

    @Autowired
    private FunctionalTestClient functionalClient;

    @SneakyThrows
    public String getDartsGatewayOperationUrl() {
        return baseUri + GATEWAY_WEB_CONTEXT;
    }

    @BeforeEach
    public void clean() throws IOException, InterruptedException {
        functionalClient.clear();
        viq.clearToken();
        xhibit.clearToken();
        cpp.clearToken();
    }
}

package uk.gov.hmcts.darts;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.common.AccessTokenClient;
import uk.gov.hmcts.darts.common.client1.ContextRegistryClientWrapper;

import java.net.URI;

@SpringBootTest(
    classes = {
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles({"functionalTest"})
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class FunctionalJMeterPerformanceTest {

    protected static final String COURTHOUSE_SWANSEA = "func-swansea";

    @Autowired
    private ContextRegistryClientWrapper viq;

    @Autowired
    private ContextRegistryClientWrapper xhibit;

    @Autowired
    private ContextRegistryClientWrapper cpp;

    @Autowired
    private AccessTokenClient client;

    @Value("${deployed-application-uri}")
    private URI baseUri;

    @BeforeEach
    void setUp() {
        configureRestAssured();
    }


    public ContextRegistryClientWrapper getContextRegistryClientWithViqClient() {
        return new ContextRegistryClientWrapper(baseUri, viq);
    }

    public ContextRegistryClientWrapper getContextRegistryClientWithCppClient() {
        return new ContextRegistryClientWrapper(baseUri, cpp);
    }

    public ContextRegistryClientWrapper getContextRegistryClientWithXhibitClient() {
        return new ContextRegistryClientWrapper(baseUri, xhibit);
    }

    public GatewayClient getGatewayClient() {
        return new GatewayClient(baseUri, xhibit);
    }

    @SneakyThrows
    public String getUri(String endpoint) {
        return baseUri + endpoint + "service/darts";
    }

    public void clean() {
        buildFunctionDartsApi()
            .baseUri(getUri("/functional-tests/clean"))
            .redirects().follow(false)
            .delete();
    }

    protected RequestSpecification buildFunctionDartsApi() {
        return RestAssured.given()
            .header("Authorization", String.format("Bearer %s", client.getAccessToken()));
    }

    private void configureRestAssured() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}

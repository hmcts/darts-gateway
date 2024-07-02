package uk.gov.hmcts.darts;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.common.AccessTokenClient;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;

import java.net.URI;

@SpringBootTest()
@ActiveProfiles({"functionalTest"})
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class FunctionalTest {

    protected static final String COURTHOUSE_SWANSEA = "func-swansea";

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

    @Value("${deployed-application-uri}")
    private URI baseUri;

    @BeforeEach
    void setUp() {
        configureRestAssured();
    }


    @SneakyThrows
    public String getBaseUri(String endpoint) {
        return baseUri + endpoint + "service/darts";
    }

    @SneakyThrows
    public String getDartsGatewayOperationUrl() {
        return baseUri + "/service/darts";
    }

    public void clean() {
        buildFunctionGatewayTestApi()
            .baseUri(getBaseUri("/functional-tests/clean"))
            .redirects().follow(false)
            .delete();
    }

    protected RequestSpecification buildFunctionDartsApi() {
        return RestAssured.given()
            .header("Authorization", String.format("Bearer %s", client.getAccessToken()));
    }

    protected RequestSpecification buildFunctionGatewayTestApi() {
        return RestAssured.given();
    }

    private void configureRestAssured() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}

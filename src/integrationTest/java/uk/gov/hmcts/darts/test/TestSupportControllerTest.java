package uk.gov.hmcts.darts.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.utils.IntegrationBase;

@ActiveProfiles("int-test-jwt-token-shared")
class TestSupportControllerTest extends IntegrationBase {

    @Test
    public void testCleanup() {
        Response response = RestAssured.given()
            .baseUri("http://localhost:" + port + "/functional-tests/clean")
            .redirects().follow(false)
            .delete();
        Assertions.assertEquals(200, response.getStatusCode());
    }
}

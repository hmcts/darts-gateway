package uk.gov.hmcts.darts.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;
import uk.gov.hmcts.darts.testutils.IntegrationBase;

@ActiveProfiles("int-test-jwt-token-shared")
class TestSupportControllerTest extends IntegrationBase {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test

    void testCleanup() {
        // add a key to the redis cache so we can prove removal
        String testKey = TokenRegisterable.CACHE_PREFIX + "testKey";
        redisTemplate.opsForValue().set(testKey, "testValue");

        // prove key is in the cache
        Assertions.assertNotNull(redisTemplate.opsForValue().get(testKey));

        // run the cleanup endpoint
        Response response = RestAssured.given()
            .baseUri("http://localhost:" + port + "/functional-tests/clean")
            .redirects().follow(false)
            .delete();

        // assert that the key is no longer in the redis cache
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertNull(redisTemplate.opsForValue().get(testKey));
    }
}

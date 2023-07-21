package uk.gov.hmcts.darts.common.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RootControllerTest {

    private static final String WELCOME_RESPONSE = "Welcome to darts-gateway";

    /** Class being tested. */
    private RootController rootController;

    @BeforeEach
    void setUp() {
        rootController = new RootController();
    }

    @Test
    void testRootController() {
        ResponseEntity<String> response = rootController.welcome();

        assertNotNull(response, "A response must be returned by Root controller");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response does not have expected status code");
        assertNotNull(response.getBody(), "Response must have a body");
        assertEquals(WELCOME_RESPONSE, response.getBody(), "Response does not contain expected message");
    }
}

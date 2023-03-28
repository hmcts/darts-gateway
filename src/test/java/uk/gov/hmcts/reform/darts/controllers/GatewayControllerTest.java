package uk.gov.hmcts.reform.darts.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;
import uk.gov.hmcts.reform.darts.validate.services.ThreateningContentService;
import uk.gov.hmcts.reform.darts.validate.services.XmlComplexityService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GatewayControllerTest {

    private static final String CONTENT_TO_VALIDATE = "some content";
    private static final String THREATENING_CONTENT_ERROR_MESSAGE = "Threatening content error message";
    private static final String XML_COMPLEXITY_ERROR_MESSAGE = "XML complexity error message";

    @Mock
    ThreateningContentService mockThreateningContentService;

    @Mock
    XmlComplexityService mockXmlComplexityService;

    /**
     * Class being tested.
     */
    GatewayController gatewayController;

    @BeforeEach
    void setUp() {
        gatewayController = new GatewayController(mockThreateningContentService, mockXmlComplexityService);
    }

    private void assertResponse(ResponseEntity<ValidateResult> response,
                                boolean expectedIsValid,
                                String expectedError) {
        assertNotNull(response, "A response must be returned by Gateway controller");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response does not have expected status code");
        ValidateResult responseBody = response.getBody();
        assertNotNull(responseBody, "Response must have a body");

        if (expectedIsValid) {
            assertTrue(responseBody.isValid(), "Response should be valid");
        } else {
            assertFalse(responseBody.isValid(), "Response should be invalid");
        }

        assertEquals(expectedError, responseBody.getError(), "Response does not have expected error message");
    }

    @Test
    void testContentValid() {
        ValidateResult resultValidateContent = new ValidateResult(true, "");
        when(mockThreateningContentService.validateContent(CONTENT_TO_VALIDATE)).thenReturn(resultValidateContent);

        ValidateResult resultXmlComplexity = new ValidateResult(true, "");
        when(mockXmlComplexityService.validateContent(CONTENT_TO_VALIDATE)).thenReturn(resultXmlComplexity);

        ResponseEntity<ValidateResult> response = gatewayController.validate(CONTENT_TO_VALIDATE);

        assertResponse(response, true, "");

        verify(mockThreateningContentService).validateContent(CONTENT_TO_VALIDATE);
        verify(mockXmlComplexityService).validateContent(CONTENT_TO_VALIDATE);
    }

    @Test
    void testContentInvalidThreateningContent() {
        ValidateResult resultValidateContent = new ValidateResult(false, THREATENING_CONTENT_ERROR_MESSAGE);
        when(mockThreateningContentService.validateContent(CONTENT_TO_VALIDATE)).thenReturn(resultValidateContent);

        ResponseEntity<ValidateResult> response = gatewayController.validate(CONTENT_TO_VALIDATE);

        assertResponse(response, false, THREATENING_CONTENT_ERROR_MESSAGE);

        verify(mockThreateningContentService).validateContent(CONTENT_TO_VALIDATE);
        verify(mockXmlComplexityService, never()).validateContent(CONTENT_TO_VALIDATE);
    }

    @Test
    void testContentInvalidXmlComplexity() {
        ValidateResult resultValidateContent = new ValidateResult(true, "");
        when(mockThreateningContentService.validateContent(CONTENT_TO_VALIDATE)).thenReturn(resultValidateContent);

        ValidateResult resultXmlComplexity = new ValidateResult(false, XML_COMPLEXITY_ERROR_MESSAGE);
        when(mockXmlComplexityService.validateContent(CONTENT_TO_VALIDATE)).thenReturn(resultXmlComplexity);

        ResponseEntity<ValidateResult> response = gatewayController.validate(CONTENT_TO_VALIDATE);

        assertResponse(response, false, XML_COMPLEXITY_ERROR_MESSAGE);

        verify(mockThreateningContentService).validateContent(CONTENT_TO_VALIDATE);
        verify(mockXmlComplexityService).validateContent(CONTENT_TO_VALIDATE);
    }
}

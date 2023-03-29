package uk.gov.hmcts.reform.darts.validate.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    @Mock
    Throwable mockThrowable;

    /** Class being tested. */
    RestExceptionHandler restExceptionHandler;

    @BeforeEach
    void setUp() {
        restExceptionHandler = new RestExceptionHandler();
    }

    @Test
    void testHandlerXmlComplexityRuleException() {
        String message = "Text XmlComplexityRuleException";
        XmlComplexityRuleException exception = new XmlComplexityRuleException(message, mockThrowable);

        ResponseEntity<String> response = restExceptionHandler.handXmlComplexityRuleException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode(), "Response does not have expected status code");
        assertEquals(message, response.getBody(), "Response does not have expected error message");

        verifyNoInteractions(mockThrowable);
    }

    @Test
    void testHandlerXmlConfigurationException() {
        String message = "Test XmlConfigurationException";
        XmlConfigurationException exception = new XmlConfigurationException(message, mockThrowable);

        ResponseEntity<String> response = restExceptionHandler.handleXmlConfigurationException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode(), "Response does not have expected status code");
        assertEquals(message, response.getBody(), "Response does not have expected error message");

        verifyNoInteractions(mockThrowable);
    }

    @Test
    void testHandlerXmlConversionException() {
        String message = "Test XmlConversionException";
        XmlConversionException exception = new XmlConversionException(message, mockThrowable);

        ResponseEntity<ValidateResult> response = restExceptionHandler.handleXmlConversionException(exception);

        assertEquals(BAD_REQUEST, response.getStatusCode(), "Response does not have expected status code");

        ValidateResult responseBody = response.getBody();
        assertNotNull(responseBody, "Response must have a body");

        assertFalse(responseBody.isValid(), "Response should be invalid");
        assertEquals(message, responseBody.getError(), "Response does not have expected error message");

        verifyNoInteractions(mockThrowable);
    }
}

package uk.gov.hmcts.reform.darts.validate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.darts.validate.domain.ValidateResult;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handXmlComplexityRuleException(XmlComplexityRuleException complexityRuleException) {
        String errorMessage = complexityRuleException.getMessage();
        log.error("XmlComplexityRuleException: {}", errorMessage);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleXmlConfigurationException(XmlConfigurationException configurationException) {
        String errorMessage = configurationException.getMessage();
        log.error("XmlConfigurationException: {}", errorMessage);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler
    public ResponseEntity<ValidateResult> handleXmlConversionException(XmlConversionException conversionException) {
        String errorMessage = conversionException.getMessage();
        log.error("XmlConversionException: {}", errorMessage);

        ValidateResult result = new ValidateResult(false, errorMessage);
        return ResponseEntity.status(BAD_REQUEST).body(result);
    }
}

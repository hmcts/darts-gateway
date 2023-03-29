package uk.gov.hmcts.reform.darts.validate.exception;

import java.io.Serial;

public class XmlConversionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5853891020654915898L;

    public XmlConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}

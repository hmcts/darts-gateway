package uk.gov.hmcts.darts.apim.validate.exception;

import java.io.Serial;

public class XmlConfigurationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7522925284063818876L;

    public XmlConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

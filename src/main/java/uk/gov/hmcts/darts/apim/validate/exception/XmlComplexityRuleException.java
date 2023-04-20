package uk.gov.hmcts.darts.apim.validate.exception;

import java.io.Serial;

public class XmlComplexityRuleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6415400036824376540L;

    public XmlComplexityRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

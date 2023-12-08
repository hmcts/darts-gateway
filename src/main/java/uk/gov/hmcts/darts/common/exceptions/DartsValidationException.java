package uk.gov.hmcts.darts.common.exceptions;

import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

public class DartsValidationException extends DartsException {

    public DartsValidationException(Throwable throwable) {
        super(throwable, null);
    }

    public DartsValidationException(Throwable cause, CodeAndMessage codeAndMessage) {
        super(cause, codeAndMessage);
    }
}

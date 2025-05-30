package uk.gov.hmcts.darts.common.client.exeption;

import lombok.Getter;
import uk.gov.hmcts.darts.common.exceptions.DartsException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@Getter
public class ClientProblemException extends DartsException {

    @SuppressWarnings("java:S1135")
    //TODO: Work out why we cant have a central problem type when generating the API spec
    private final transient Problem problem;

    public ClientProblemException(Throwable cause, String message, CodeAndMessage codeAndMessage, Problem problem) {
        super(cause, message, codeAndMessage);
        this.problem = problem;
    }

    public ClientProblemException(Throwable cause, CodeAndMessage codeAndMessage, Problem problem) {
        this(cause, codeAndMessage.getMessage(), codeAndMessage, problem);
    }

    public ClientProblemException(CodeAndMessage codeAndMessage, Problem problem) {
        this(null, codeAndMessage, problem);
    }

    public ClientProblemException(Problem problem) {
        this(CodeAndMessage.ERROR, problem);
    }
}

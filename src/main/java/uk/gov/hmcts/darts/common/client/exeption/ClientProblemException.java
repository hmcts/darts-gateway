package uk.gov.hmcts.darts.common.client.exeption;

import lombok.Getter;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

@Getter
public class ClientProblemException extends DartsException {

    //TODO: Work out why we cant have a central problem type when generating the API spec
    private Problem problem;

    public ClientProblemException(Throwable cause, CodeAndMessage codeAndMessage, Problem problem) {
        super(cause, codeAndMessage);
        this.problem = problem;
    }

    public ClientProblemException(CodeAndMessage codeAndMessage, Problem problem) {
        this(null, codeAndMessage, problem);
    }

    public ClientProblemException(Problem problem) {
        this(CodeAndMessage.ERROR, problem);
    }
}

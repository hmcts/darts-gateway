package uk.gov.hmcts.darts.common.client1.exeption.event;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client1.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.PostCourtLogsErrorCode;

@Getter
@SuppressWarnings("java:S110")
public class EventAPIPostCourtLogException extends ClientProblemException {
    private final transient ProblemResponseMapping<PostCourtLogsErrorCode> mapping;

    public EventAPIPostCourtLogException(Throwable cause, ProblemResponseMapping<PostCourtLogsErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);
        this.mapping = mapping;
    }

    public EventAPIPostCourtLogException(ProblemResponseMapping<PostCourtLogsErrorCode> mapping, Problem problem) {
        this(null, mapping, problem);
    }
}

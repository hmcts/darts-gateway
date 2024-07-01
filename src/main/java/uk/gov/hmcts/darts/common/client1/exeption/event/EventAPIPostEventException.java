package uk.gov.hmcts.darts.common.client1.exeption.event;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client1.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.EventErrorCode;

@Getter
@SuppressWarnings("java:S110")
public class EventAPIPostEventException extends ClientProblemException {
    private final transient ProblemResponseMapping<EventErrorCode> mapping;

    public EventAPIPostEventException(Throwable cause, ProblemResponseMapping<EventErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);
        this.mapping = mapping;
    }

    public EventAPIPostEventException(ProblemResponseMapping<EventErrorCode> mapping, Problem problem) {
        this(null, mapping, problem);
    }
}

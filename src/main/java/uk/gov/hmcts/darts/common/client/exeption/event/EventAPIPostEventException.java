package uk.gov.hmcts.darts.common.client.exeption.event;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.EventErrorCode;

@Getter
public class EventAPIPostEventException extends ClientProblemException {
    ProblemResponseMapping<EventErrorCode> mapping;

    public EventAPIPostEventException(Throwable cause, ProblemResponseMapping<EventErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);
        this.mapping = mapping;
    }

    public EventAPIPostEventException(ProblemResponseMapping<EventErrorCode> mapping, Problem problem) {
        this(null, mapping, problem);
    }
}

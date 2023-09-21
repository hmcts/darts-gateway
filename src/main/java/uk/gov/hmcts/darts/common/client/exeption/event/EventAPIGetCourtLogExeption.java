package uk.gov.hmcts.darts.common.client.exeption.event;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.GetCourtLogsErrorCode;

@Getter
@SuppressWarnings("java:S110")
public class EventAPIGetCourtLogExeption extends ClientProblemException {
    private final transient ProblemResponseMapping<GetCourtLogsErrorCode> mapping;

    public EventAPIGetCourtLogExeption(Throwable cause, ProblemResponseMapping<GetCourtLogsErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);

        this.mapping = mapping;

    }

    public EventAPIGetCourtLogExeption(ProblemResponseMapping<GetCourtLogsErrorCode> mapping, Problem problem) {
        this(null, mapping, problem);
    }
}

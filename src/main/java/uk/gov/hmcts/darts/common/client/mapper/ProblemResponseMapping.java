package uk.gov.hmcts.darts.common.client.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@Value
@Builder
@Getter
public class ProblemResponseMapping<T> {
     T problem;
     CodeAndMessage message;

    public boolean match(Problem problemToMatch)
    {
        return problemToMatch.getType().toString().equals(problem.toString());
    }
}

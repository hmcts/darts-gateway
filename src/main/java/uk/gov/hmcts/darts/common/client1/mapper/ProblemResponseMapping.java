package uk.gov.hmcts.darts.common.client1.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@Value
@Builder
@Getter
public class ProblemResponseMapping<T> {
    T problem;
    CodeAndMessage message;

    public boolean match(Problem problemToMatch) {
        if (problemToMatch.getType() == null) {
            return false;
        }
        return StringUtils.equals(problemToMatch.getType().toString(), problem.toString());
    }

    public boolean match(String code) {
        return code.equals(problem.toString());
    }
}

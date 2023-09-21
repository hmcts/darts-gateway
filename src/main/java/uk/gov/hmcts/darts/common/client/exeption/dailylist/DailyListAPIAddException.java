package uk.gov.hmcts.darts.common.client.exeption.dailylist;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;

@Getter
@SuppressWarnings("java:S110")
public class DailyListAPIAddException extends ClientProblemException {
    private final transient  ProblemResponseMapping<PostDailyListErrorCode> mapping;

    public DailyListAPIAddException(Throwable cause, ProblemResponseMapping<PostDailyListErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);
        this.mapping = mapping;
    }

    public DailyListAPIAddException(ProblemResponseMapping<PostDailyListErrorCode> addDailyList, Problem problem) {
        this(null, addDailyList, problem);
    }
}

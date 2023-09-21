package uk.gov.hmcts.darts.common.client.exeption.cases;

import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.cases.GetCasesErrorCode;

@Getter
@SuppressWarnings("java:S110")
public class CasesAPIGetCasesException extends ClientProblemException {
    private final transient ProblemResponseMapping<GetCasesErrorCode> mapping;

    public CasesAPIGetCasesException(Throwable cause, ProblemResponseMapping<GetCasesErrorCode> addCases, Problem problem) {
        super(cause, addCases.getMessage(), problem);
        this.mapping = addCases;
    }

    public CasesAPIGetCasesException(ProblemResponseMapping<GetCasesErrorCode> addCases, Problem problem) {
        this(null, addCases, problem);
    }
}

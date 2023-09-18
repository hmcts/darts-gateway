package uk.gov.hmcts.darts.common.client.exeption.cases;


import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.mapper.ProblemResponseMapping;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.cases.PostCasesErrorCode;

@Getter
public class CasesAPIPostCaseException extends ClientProblemException {
    private ProblemResponseMapping<PostCasesErrorCode> mapping;

    public CasesAPIPostCaseException(Throwable cause, ProblemResponseMapping<PostCasesErrorCode> mapping, Problem problem) {
        super(cause, mapping.getMessage(), problem);
        this.mapping = mapping;
    }

    public CasesAPIPostCaseException(ProblemResponseMapping<PostCasesErrorCode> addCases, Problem problem) {
        this(null, addCases, problem);
    }
}

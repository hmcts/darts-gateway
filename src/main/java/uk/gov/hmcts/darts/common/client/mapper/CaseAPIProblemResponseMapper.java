package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.cases.CasesAPIGetCasesException;
import uk.gov.hmcts.darts.common.client.exeption.cases.CasesAPIPostCaseException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.cases.GetCasesErrorCode;
import uk.gov.hmcts.darts.model.cases.PostCasesErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.Optional;

public class CaseAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {

    {
        addMapper(PostCasesErrorCode.class, getBuilderAddCase()
            .problem(PostCasesErrorCode.CASE_DOCUMENT_CANT_BE_PARSED).message(CodeAndMessage.INVALID_XML).build());

        addMapper(PostCasesErrorCode.class, getBuilderAddCase()
            .problem(PostCasesErrorCode.CASE_COURT_HOUSE_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addMapper(GetCasesErrorCode.class, getBuilderGetCases()
            .problem(GetCasesErrorCode.CASE_COURT_HOUSE_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());
    }

    private ProblemResponseMapping.ProblemResponseMappingBuilder<PostCasesErrorCode> getBuilderAddCase() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }

    private ProblemResponseMapping.ProblemResponseMappingBuilder<GetCasesErrorCode> getBuilderGetCases() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }

    @Override
    public Optional<ClientProblemException> getExceptionForProblem(Problem problem) {

        return getProblemValueForProblem(PostCasesErrorCode.class, problem,
                                         (mapping) -> new CasesAPIPostCaseException(mapping, problem)
        )
            .or(() -> getProblemValueForProblem(GetCasesErrorCode.class, problem,
                                                (mapping) -> new CasesAPIGetCasesException(mapping, problem)
            ));
    }
}

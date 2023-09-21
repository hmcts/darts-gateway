package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.cases.CasesAPIGetCasesException;
import uk.gov.hmcts.darts.common.client.exeption.cases.CasesAPIPostCaseException;
import uk.gov.hmcts.darts.model.cases.GetCasesErrorCode;
import uk.gov.hmcts.darts.model.cases.PostCasesErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("unchecked")
public class CaseAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {

    {
        // create the operation and mappings for the post case
        var opmapping = new ProblemResponseMappingOperation
            .ProblemResponseMappingOperationBuilder<PostCasesErrorCode>()
            .operation(PostCasesErrorCode.class)
            .exception(this::createPostCaseException).build();

        // create mappings
        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(PostCasesErrorCode.CASE_DOCUMENT_CANT_BE_PARSED)
                                 .message(CodeAndMessage.INVALID_XML).build());

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(PostCasesErrorCode.CASE_COURT_HOUSE_NOT_FOUND)
                                 .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());
        addOperationMappings(opmapping);

        var getCaseOp = new ProblemResponseMappingOperation
            .ProblemResponseMappingOperationBuilder<GetCasesErrorCode>()
            .operation(GetCasesErrorCode.class)
            .exception(this::createAPIGetCasesException).build();


        getCaseOp.addMapping(getCaseOp.createProblemResponseMapping()
                                 .problem(GetCasesErrorCode.CASE_COURT_HOUSE_NOT_FOUND)
                                 .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addOperationMappings(getCaseOp);
    }

    private CasesAPIPostCaseException createPostCaseException(ProblemAndMapping problemAndMapping)
    {
        return new CasesAPIPostCaseException(
            (ProblemResponseMapping<PostCasesErrorCode>) problemAndMapping.getMapping(), problemAndMapping.getProblem());
    }

    private CasesAPIGetCasesException createAPIGetCasesException(ProblemAndMapping problemAndMapping) {
        return new CasesAPIGetCasesException(
            (ProblemResponseMapping<GetCasesErrorCode>) problemAndMapping.getMapping(), problemAndMapping.getProblem());
    }
}

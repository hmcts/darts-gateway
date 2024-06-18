package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.common.CommonErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("PMD.NonStaticInitializer")
public class CommonApiProblemResponseMapper extends AbstractAPIProblemResponseMapper {

    {
        ProblemResponseMappingOperation<CommonErrorCode> getCaseOp = new ProblemResponseMappingOperation
            .ProblemResponseMappingOperationBuilder<CommonErrorCode>()
            .operation(CommonErrorCode.class)
            .exception(this::createException).build();

        getCaseOp.addMapping(getCaseOp.createProblemResponseMapping()
                                 .problem(CommonErrorCode.COURTHOUSE_PROVIDED_DOES_NOT_EXIST)
                                 .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addOperationMappings(getCaseOp);
    }

    private ClientProblemException createException(ProblemAndMapping problemAndMapping) {
        return new ClientProblemException(problemAndMapping.getMapping().getMessage(), problemAndMapping.getProblem());
    }
}

package uk.gov.hmcts.darts.common.client1.mapper;

import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.dailylist.DailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("unchecked")
public class DailyListAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {
    {
        var opmapping = new ProblemResponseMappingOperation
            . ProblemResponseMappingOperationBuilder<DailyListErrorCode>()
            .operation(DailyListErrorCode.class)
            .exception(this::createException).build();

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(DailyListErrorCode.FAILED_TO_PROCESS_DAILYLIST)
                                 .message(CodeAndMessage.NOT_FOUND_HANLDER).build());

        addOperationMappings(opmapping);
    }

    private ClientProblemException createException(ProblemAndMapping problemAndMapping) {
        return new ClientProblemException(problemAndMapping.getMapping().getMessage(), problemAndMapping.getProblem());
    }
}

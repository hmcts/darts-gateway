package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.dailylist.DailyListErrorCode;
import uk.gov.hmcts.darts.model.dailylist.DailyListTitleErrors;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("PMD.NonStaticInitializer")
public class DailyListAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {
    {
        ProblemResponseMappingOperation<DailyListErrorCode> opmapping = new ProblemResponseMappingOperation
            .ProblemResponseMappingOperationBuilder<DailyListErrorCode>()
            .operation(DailyListErrorCode.class)
            .exception(this::createException).build();

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(DailyListErrorCode.FAILED_TO_PROCESS_DAILYLIST)
                                 .message(CodeAndMessage.ERROR).build());

        addOperationMappings(opmapping);
    }

    private ClientProblemException createException(ProblemAndMapping problemAndMapping) {
        return new ClientProblemException(
            null,
            DailyListTitleErrors.FAILED_TO_PROCESS_DAILYLIST.getValue(),
            problemAndMapping.getMapping().getMessage(),
            problemAndMapping.getProblem());
    }
}

package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.dailylist.DailyListAPIAddException;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("unchecked")
public class DailyListAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {
    {
        var opmapping = new ProblemResponseMappingOperation
            . ProblemResponseMappingOperationBuilder<PostDailyListErrorCode>()
            .operation(PostDailyListErrorCode.class)
            .exception((mapping) -> new DailyListAPIAddException(
                (ProblemResponseMapping<PostDailyListErrorCode>) mapping.getMapping(), mapping.getProblem())).build();

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                       .problem(PostDailyListErrorCode.DAILYLIST_DOCUMENT_CANT_BE_PARSED)
                       .message(CodeAndMessage.INVALID_XML).build());

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(PostDailyListErrorCode.DAILYLIST_PROCESSOR_NOT_FOUND)
                                 .message(CodeAndMessage.NOT_FOUND_HANLDER).build());

        opmapping.addMapping(opmapping.createProblemResponseMapping()
                                 .problem(PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND)
                                 .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addOperationMappings(opmapping);
    }
}

package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.dailylist.DailyListAPIAddException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.Optional;

public class DailyListAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {
    {
        addMapper(PostDailyListErrorCode.class, getBuilder().problem(
                PostDailyListErrorCode.DAILYLIST_DOCUMENT_CANT_BE_PARSED)
            .message(CodeAndMessage.INVALID_XML).build());

        addMapper(PostDailyListErrorCode.class, getBuilder().
            problem(PostDailyListErrorCode.DAILYLIST_PROCESSOR_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_HANLDER).build());

        addMapper(PostDailyListErrorCode.class, getBuilder().
            problem(PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());
    }

    private ProblemResponseMapping.ProblemResponseMappingBuilder<PostDailyListErrorCode> getBuilder() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }

    @Override
    public Optional<ClientProblemException> getExceptionForProblem(Problem p) {
        return getProblemValueForProblem(PostDailyListErrorCode.class, p,
                                         (mapping) -> new DailyListAPIAddException(mapping, p)
        );
    }
}

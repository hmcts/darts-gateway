package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.dailylist.DailyListAPIAddException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

class APIResponseMapperTest {

    @Test
    void testProblemNoCourtHouse() {
        Problem problem = new Problem();
        PostDailyListErrorCode problemCode = PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<? extends ProblemResponseMapping<?>> mapping = new DummyAPIResponseMapper().getMapping(problem);
        Assertions.assertTrue(mapping.isPresent());
        CodeAndMessage message = mapping.get().getMessage();
        Assertions.assertEquals(CodeAndMessage.NOT_FOUND_COURTHOUSE, message);
    }

    @Test
    void testNoProblemFound() {
        Problem problem = new Problem();
        PostDailyListErrorCode problemCode = PostDailyListErrorCode.DAILYLIST_DOCUMENT_CANT_BE_PARSED;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<? extends ProblemResponseMapping<?>> mapping = new DummyAPIResponseMapper().getMapping(problem);
        Assertions.assertFalse(mapping.isPresent());
    }

    @Test
    void testProblemNoCourtHouseException() {
        Problem problem = new Problem();
        PostDailyListErrorCode problemCode = PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = new DummyAPIResponseMapper().getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(CodeAndMessage.NOT_FOUND_COURTHOUSE, exception.get().getCodeAndMessage());
        Assertions.assertEquals(problem, exception.get().getProblem());
    }

    @SuppressWarnings("unchecked")
    class DummyAPIResponseMapper extends AbstractAPIProblemResponseMapper {
        {
            var opmapping = new ProblemResponseMappingOperation
                    . ProblemResponseMappingOperationBuilder<PostDailyListErrorCode>()
                    .operation(PostDailyListErrorCode.class)
                    .exception((mapping) -> new DailyListAPIAddException(
                            (ProblemResponseMapping<PostDailyListErrorCode>) mapping.getMapping(), mapping.getProblem())).build();

            opmapping.addMapping(opmapping.createProblemResponseMapping()
                    .problem(PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND)
                    .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

            addOperationMappings(opmapping);
        }
    }
}

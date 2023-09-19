package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
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
        Optional<CodeAndMessage> message = new DummyAPIResponseMapper().getCodeAndMessage(problem);
        Assertions.assertTrue(message.isPresent());
        Assertions.assertEquals(CodeAndMessage.NOT_FOUND_COURTHOUSE, message.get());
    }

    @Test
    void testNoProblemFound() {
        Problem problem = new Problem();
        PostDailyListErrorCode problemCode = PostDailyListErrorCode.DAILYLIST_DOCUMENT_CANT_BE_PARSED;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<CodeAndMessage> message = new DummyAPIResponseMapper().getCodeAndMessage(problem);
        Assertions.assertTrue(message.isEmpty());
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

    class DummyAPIResponseMapper extends AbstractAPIProblemResponseMapper {
        {
            addMapper(
                PostDailyListErrorCode.class,
                getBuilder().problem(PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND).message(
                    CodeAndMessage.NOT_FOUND_COURTHOUSE).build()
            );
        }

        private ProblemResponseMapping.ProblemResponseMappingBuilder<PostDailyListErrorCode> getBuilder() {
            return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
        }

        @Override
        public Optional<ClientProblemException> getExceptionForProblem(Problem problem) {
            return getProblemValueForProblem(
                PostDailyListErrorCode.class,
                problem,
                (mapping) -> new ClientProblemException(mapping.getMessage(), problem)
            );
        }
    }
}

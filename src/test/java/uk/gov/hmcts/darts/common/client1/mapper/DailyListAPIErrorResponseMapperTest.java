package uk.gov.hmcts.darts.common.client1.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.dailylist.DailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

class DailyListAPIErrorResponseMapperTest {
    private DailyListAPIProblemResponseMapper responseMapper;

    private Problem problem;

    @BeforeEach
    public void before() {
        responseMapper = new DailyListAPIProblemResponseMapper();
        problem = new Problem();
    }

    @Test
    void testCourtHandlerNotFoundForAddDailyList() {
        DailyListErrorCode problemCode = DailyListErrorCode.FAILED_TO_PROCESS_DAILYLIST;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, exception.get().getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_HANLDER,
            exception.get().getCodeAndMessage()
        );
    }
}

package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.dailylist.DailyListErrorCode;
import uk.gov.hmcts.darts.model.dailylist.DailyListTitleErrors;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Optional<ClientProblemException> exceptionOpt = responseMapper.getExceptionForProblem(problem);
        assertTrue(exceptionOpt.isPresent());
        ClientProblemException exception = exceptionOpt.get();
        assertEquals(problem, exception.getProblem());
        assertEquals(
            CodeAndMessage.ERROR,
            exception.getCodeAndMessage()
        );
        assertEquals(
            DailyListTitleErrors.FAILED_TO_PROCESS_DAILYLIST.getValue(),
            exception.getMessage()
        );
    }
}

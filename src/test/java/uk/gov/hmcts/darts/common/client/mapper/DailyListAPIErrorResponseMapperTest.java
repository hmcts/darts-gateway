package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.dailylist.DailyListAPIAddException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

public class DailyListAPIErrorResponseMapperTest {
    private DailyListAPIProblemResponseMapper responseMapper;

    private Problem problem;

    @BeforeEach
    public void before()
    {
        responseMapper = new DailyListAPIProblemResponseMapper();
        problem = new Problem();
    }

    @Test
    public void testDocumentParseExceptionForAddDailyList(){
        Problem problem = new Problem();
        problem.setType(URI.create(PostDailyListErrorCode.DAILYLIST_DOCUMENT_CANT_BE_PARSED.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, ((DailyListAPIAddException)exception.get()).getProblem());
        Assertions.assertEquals(CodeAndMessage.INVALID_XML, ((DailyListAPIAddException)exception.get()).getMapping().getMessage());
        Assertions.assertEquals(PostDailyListErrorCode.DAILYLIST_DOCUMENT_CANT_BE_PARSED, ((DailyListAPIAddException)exception.get()).getMapping().getProblem());
    }

    @Test
    public void testCourtNotFoundForAddDailyList(){
        Problem problem = new Problem();
        problem.setType(URI.create(PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, ((DailyListAPIAddException)exception.get()).getProblem());
        Assertions.assertEquals(CodeAndMessage.NOT_FOUND_COURTHOUSE, ((DailyListAPIAddException)exception.get()).getMapping().getMessage());
        Assertions.assertEquals(PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND, ((DailyListAPIAddException)exception.get()).getMapping().getProblem());
    }

    @Test
    public void testCourtHandlerNotFoundForAddDailyList(){
        Problem problem = new Problem();
        problem.setType(URI.create(PostDailyListErrorCode.DAILYLIST_PROCESSOR_NOT_FOUND.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, ((DailyListAPIAddException)exception.get()).getProblem());
        Assertions.assertEquals(CodeAndMessage.NOT_FOUND_HANLDER, ((DailyListAPIAddException)exception.get()).getMapping().getMessage());
        Assertions.assertEquals(PostDailyListErrorCode.DAILYLIST_PROCESSOR_NOT_FOUND, ((DailyListAPIAddException)exception.get()).getMapping().getProblem());
    }
}
package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.cases.CasesAPIPostCaseException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.cases.PostCasesErrorCode;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

class AudioAPIErrorResponseMapperTest {

    private AudioAPIProblemResponseMapper responseMapper;

    private Problem problem;

    @BeforeEach
    void before() {
        responseMapper = new AudioAPIProblemResponseMapper();
        problem = new Problem();
    }

    @Test
    void testNoExceptionForProblem() {
        PostDailyListErrorCode problemCode = PostDailyListErrorCode.DAILYLIST_COURT_HOUSE_NOT_FOUND;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isEmpty());
    }

    @Test
    void testExceptionForAddCaseProblemNoCourtHouse() {
        PostCasesErrorCode problemCode = PostCasesErrorCode.CASE_COURT_HOUSE_NOT_FOUND;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, ((CasesAPIPostCaseException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_COURTHOUSE,
            ((CasesAPIPostCaseException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            PostCasesErrorCode.CASE_COURT_HOUSE_NOT_FOUND,
            ((CasesAPIPostCaseException) exception.get()).getMapping().getProblem()
        );
    }
}

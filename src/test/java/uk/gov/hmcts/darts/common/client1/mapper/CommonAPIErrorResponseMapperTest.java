package uk.gov.hmcts.darts.common.client1.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.common.CommonErrorCode;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

class CommonAPIErrorResponseMapperTest {

    private CommonApiProblemResponseMapper responseMapper;

    private Problem problem;

    @BeforeEach
    void before() {
        responseMapper = new CommonApiProblemResponseMapper();
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
    void testExceptionForAddAudioProblemNoCourtHouse() {
        CommonErrorCode problemCode = CommonErrorCode.COURTHOUSE_PROVIDED_DOES_NOT_EXIST;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_COURTHOUSE,
            exception.get().getCodeAndMessage()
        );
    }
}

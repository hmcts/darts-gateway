package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.addaudio.AudioAPIAddAudioException;
import uk.gov.hmcts.darts.common.client.exeption.cases.CasesAPIPostCaseException;
import uk.gov.hmcts.darts.model.audio.AddAudioErrorCode;
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
    void testExceptionForAddAudioProblemNoCourtHouse() {
        AddAudioErrorCode problemCode = AddAudioErrorCode.ADD_AUDIO_COURT_HOUSE_NOT_FOUND;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, ((AudioAPIAddAudioException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_COURTHOUSE,
            ((AudioAPIAddAudioException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            AddAudioErrorCode.ADD_AUDIO_COURT_HOUSE_NOT_FOUND,
            ((AudioAPIAddAudioException) exception.get()).getMapping().getProblem()
        );
    }
}

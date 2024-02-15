package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.EventErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

class EventAPIErrorResponseMapperTest {

    private EventAPIProblemResponseMapper responseMapper;

    private Problem problem;

    @BeforeEach
    public void before() {
        responseMapper = new EventAPIProblemResponseMapper();
        problem = new Problem();
    }

    @Test
    void testProcessorNotFoundForNewEvent() {
        EventErrorCode problemCode = EventErrorCode.EVENT_HANDLER_NOT_FOUND_IN_DB;
        URI uriType = URI.create(problemCode.getValue());
        problem.setType(uriType);
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertEquals(problem, ((ClientProblemException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_HANLDER,
            ((ClientProblemException) exception.get()).getCodeAndMessage()
        );
    }
}

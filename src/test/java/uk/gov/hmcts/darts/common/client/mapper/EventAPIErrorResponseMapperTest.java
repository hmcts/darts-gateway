package uk.gov.hmcts.darts.common.client.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.darts.common.client.exeption.*;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIGetCourtLogExeption;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIPostCourtLogException;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIPostEventException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.EventErrorCode;
import uk.gov.hmcts.darts.model.event.GetCourtLogsErrorCode;
import uk.gov.hmcts.darts.model.event.PostCourtLogsErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.net.URI;
import java.util.Optional;

public class EventAPIErrorResponseMapperTest {

    private EventAPIProblemResponseMapper responseMapper;

    private Problem problem;

    @BeforeEach
    public void before() {
        responseMapper = new EventAPIProblemResponseMapper();
        problem = new Problem();
    }

    @Test
    public void testProcessorNotFoundForNewEvent() {
        Problem problem = new Problem();
        problem.setType(URI.create(EventErrorCode.PROCESSOR_NOT_FOUND.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertTrue(exception.get() instanceof EventAPIPostEventException);
        Assertions.assertEquals(problem, ((EventAPIPostEventException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_HANLDER,
            ((EventAPIPostEventException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            EventErrorCode.PROCESSOR_NOT_FOUND,
            ((EventAPIPostEventException) exception.get()).getMapping().getProblem()
        );
    }

    @Test
    public void testCourtNotFoundForNewEvent() {
        Problem problem = new Problem();
        problem.setType(URI.create(EventErrorCode.EVENT_COURT_HOUSE_NOT_FOUND.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertTrue(exception.get() instanceof EventAPIPostEventException);
        Assertions.assertEquals(problem, ((EventAPIPostEventException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_COURTHOUSE,
            ((EventAPIPostEventException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            EventErrorCode.EVENT_COURT_HOUSE_NOT_FOUND,
            ((EventAPIPostEventException) exception.get()).getMapping().getProblem()
        );
    }

    @Test
    public void testCourtDocumentCantBeParsedForNewEvent() {
        Problem problem = new Problem();
        problem.setType(URI.create(EventErrorCode.EVENT_DOCUMENT_CANT_PARSED.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertTrue(exception.get() instanceof EventAPIPostEventException);
        Assertions.assertEquals(problem, ((EventAPIPostEventException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.INVALID_XML,
            ((EventAPIPostEventException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            EventErrorCode.EVENT_DOCUMENT_CANT_PARSED,
            ((EventAPIPostEventException) exception.get()).getMapping().getProblem()
        );
    }

    @Test
    public void testGetCourtLogs() {
        Problem problem = new Problem();
        problem.setType(URI.create(GetCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertTrue(exception.get() instanceof EventAPIGetCourtLogExeption);
        Assertions.assertEquals(problem, ((EventAPIGetCourtLogExeption) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_COURTHOUSE,
            ((EventAPIGetCourtLogExeption) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            GetCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND,
            ((EventAPIGetCourtLogExeption) exception.get()).getMapping().getProblem()
        );
    }

    @Test
    public void testDocumentCantBeParsedWhenAddingCourtLogs() {
        Problem problem = new Problem();
        problem.setType(URI.create(PostCourtLogsErrorCode.COURTLOG_DOCUMENT_CANT_BE_PARSED.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertTrue(exception.get() instanceof EventAPIGetCourtLogExeption);
        Assertions.assertEquals(problem, ((EventAPIPostCourtLogException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.INVALID_XML,
            ((EventAPIPostCourtLogException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            PostCourtLogsErrorCode.COURTLOG_DOCUMENT_CANT_BE_PARSED,
            ((EventAPIPostCourtLogException) exception.get()).getMapping().getProblem()
        );
    }

    @Test
    public void testCourtHouseNotFoundWhenAddingCourtLogs() {
        Problem problem = new Problem();
        problem.setType(URI.create(PostCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND.getValue()));
        Optional<ClientProblemException> exception = responseMapper.getExceptionForProblem(problem);
        Assertions.assertTrue(exception.isPresent());
        Assertions.assertTrue(exception.get() instanceof EventAPIGetCourtLogExeption);
        Assertions.assertEquals(problem, ((EventAPIPostCourtLogException) exception.get()).getProblem());
        Assertions.assertEquals(
            CodeAndMessage.NOT_FOUND_COURTHOUSE,
            ((EventAPIPostCourtLogException) exception.get()).getMapping().getMessage()
        );
        Assertions.assertEquals(
            PostCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND,
            ((EventAPIPostCourtLogException) exception.get()).getMapping().getProblem()
        );
    }
}

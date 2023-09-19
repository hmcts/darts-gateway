package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIGetCourtLogExeption;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIPostCourtLogException;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIPostEventException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.model.event.EventErrorCode;
import uk.gov.hmcts.darts.model.event.GetCourtLogsErrorCode;
import uk.gov.hmcts.darts.model.event.PostCourtLogsErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.Optional;


public class EventAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {

    {
        addMapper(EventErrorCode.class, getSendErrorCodeBuilder().problem(EventErrorCode.EVENT_DOCUMENT_CANT_PARSED)
            .message(CodeAndMessage.INVALID_XML).build());

        addMapper(EventErrorCode.class, getSendErrorCodeBuilder()
            .problem(EventErrorCode.PROCESSOR_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_HANLDER).build());

        addMapper(EventErrorCode.class, getSendErrorCodeBuilder()
            .problem(EventErrorCode.EVENT_COURT_HOUSE_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addMapper(GetCourtLogsErrorCode.class, getCourtLogsBuilder()
            .problem(GetCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addMapper(PostCourtLogsErrorCode.class, getPostCourtLogsBuilder()
            .problem(PostCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND).message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addMapper(PostCourtLogsErrorCode.class, getPostCourtLogsBuilder()
            .problem(PostCourtLogsErrorCode.COURTLOG_DOCUMENT_CANT_BE_PARSED).message(CodeAndMessage.INVALID_XML).build());
    }

    private ProblemResponseMapping.ProblemResponseMappingBuilder<EventErrorCode> getSendErrorCodeBuilder() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }

    private ProblemResponseMapping.ProblemResponseMappingBuilder<GetCourtLogsErrorCode> getCourtLogsBuilder() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }

    private ProblemResponseMapping.ProblemResponseMappingBuilder<PostCourtLogsErrorCode> getPostCourtLogsBuilder() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }

    @Override
    public Optional<ClientProblemException> getExceptionForProblem(Problem problem) {
        Optional<ClientProblemException> exception;

        exception = getProblemValueForProblem(
            EventErrorCode.class,
            problem,
            (mapping) -> new EventAPIPostEventException(mapping, problem)
        )
            .or(() -> getProblemValueForProblem(
                GetCourtLogsErrorCode.class,
                problem,
                (mapping) -> new EventAPIGetCourtLogExeption(mapping, problem)
            ))
            .or(() -> getProblemValueForProblem(
                PostCourtLogsErrorCode.class,
                problem,
                (mapping) -> new EventAPIPostCourtLogException(mapping, problem)
            ));

        return exception;
    }
}

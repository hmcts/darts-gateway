package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIGetCourtLogExeption;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIPostCourtLogException;
import uk.gov.hmcts.darts.common.client.exeption.event.EventAPIPostEventException;
import uk.gov.hmcts.darts.model.event.EventErrorCode;
import uk.gov.hmcts.darts.model.event.GetCourtLogsErrorCode;
import uk.gov.hmcts.darts.model.event.PostCourtLogsErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("unchecked")
public class EventAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {

    {
        // configure mappers for post event
        var postEventOp = new ProblemResponseMappingOperation
                .ProblemResponseMappingOperationBuilder<EventErrorCode>()
            .operation(EventErrorCode.class)
            .exception(this::createPostEventException).build();

        // create mappings
        postEventOp.addMapping(postEventOp.createProblemResponseMapping()
                                   .problem(EventErrorCode.EVENT_DOCUMENT_CANT_PARSED)
                                   .message(CodeAndMessage.INVALID_XML).build());

        postEventOp.addMapping(postEventOp.createProblemResponseMapping()
                                   .problem(EventErrorCode.PROCESSOR_NOT_FOUND)
                                   .message(CodeAndMessage.NOT_FOUND_HANLDER).build());

        postEventOp.addMapping(postEventOp.createProblemResponseMapping()
                                   .problem(EventErrorCode.EVENT_COURT_HOUSE_NOT_FOUND)
                                   .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addOperationMappings(postEventOp);

        // configure mappers for get court logs
        var getCourtLog = new ProblemResponseMappingOperation
                .ProblemResponseMappingOperationBuilder<GetCourtLogsErrorCode>()
            .operation(GetCourtLogsErrorCode.class)
            .exception(this::createGetCourtLogs).build();

        getCourtLog.addMapping(getCourtLog.createProblemResponseMapping()
                                   .problem(GetCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND)
                                   .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addOperationMappings(getCourtLog);

        // configure mappers for post court logs
        var postCourtLog = new ProblemResponseMappingOperation
                .ProblemResponseMappingOperationBuilder<PostCourtLogsErrorCode>()
            .operation(PostCourtLogsErrorCode.class)
            .exception(this::createPostCourtLogs).build();

        postCourtLog.addMapping(postCourtLog.createProblemResponseMapping()
                                    .problem(PostCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND)
                                    .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        postCourtLog.addMapping(postCourtLog.createProblemResponseMapping()
                                    .problem(PostCourtLogsErrorCode.COURTLOG_DOCUMENT_CANT_BE_PARSED)
                                    .message(CodeAndMessage.INVALID_XML).build());

        addOperationMappings(postCourtLog);
    }

    private EventAPIPostEventException createPostEventException(ProblemAndMapping problemAndMapping) {
        return new EventAPIPostEventException(
            (ProblemResponseMapping<EventErrorCode>) problemAndMapping.getMapping(), problemAndMapping.getProblem());
    }

    private EventAPIGetCourtLogExeption createGetCourtLogs(ProblemAndMapping problemAndMapping) {
        return new EventAPIGetCourtLogExeption(
            (ProblemResponseMapping<GetCourtLogsErrorCode>) problemAndMapping.getMapping(), problemAndMapping.getProblem());
    }

    private EventAPIPostCourtLogException createPostCourtLogs(ProblemAndMapping problemAndMapping) {
        return new EventAPIPostCourtLogException(
            (ProblemResponseMapping<PostCourtLogsErrorCode>) problemAndMapping.getMapping(), problemAndMapping.getProblem());
    }
}

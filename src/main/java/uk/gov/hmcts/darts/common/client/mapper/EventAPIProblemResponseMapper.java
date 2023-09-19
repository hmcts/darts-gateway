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
            .exception((mapping) -> new EventAPIPostEventException(
                (ProblemResponseMapping<EventErrorCode>) mapping.getMapping(), mapping.getProblem())).build();

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
            .exception((mapping) -> new EventAPIGetCourtLogExeption(
                (ProblemResponseMapping<GetCourtLogsErrorCode>) mapping.getMapping(), mapping.getProblem())).build();

        getCourtLog.addMapping(getCourtLog.createProblemResponseMapping()
                                   .problem(GetCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND)
                                   .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        addOperationMappings(getCourtLog);

        // configure mappers for post court logs
        var postCourtLog = new ProblemResponseMappingOperation
                .ProblemResponseMappingOperationBuilder<PostCourtLogsErrorCode>()
            .operation(PostCourtLogsErrorCode.class)
            .exception((mapping) -> new EventAPIPostCourtLogException(
                (ProblemResponseMapping<PostCourtLogsErrorCode>) mapping.getMapping(), mapping.getProblem())).build();

        postCourtLog.addMapping(postCourtLog.createProblemResponseMapping()
                                    .problem(PostCourtLogsErrorCode.COURTLOG_COURT_HOUSE_NOT_FOUND)
                                    .message(CodeAndMessage.NOT_FOUND_COURTHOUSE).build());

        postCourtLog.addMapping(postCourtLog.createProblemResponseMapping()
                                    .problem(PostCourtLogsErrorCode.COURTLOG_DOCUMENT_CANT_BE_PARSED)
                                    .message(CodeAndMessage.INVALID_XML).build());

        addOperationMappings(postCourtLog);
    }
}

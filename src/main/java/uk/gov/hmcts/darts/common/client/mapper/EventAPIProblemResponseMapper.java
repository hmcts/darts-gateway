package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.event.EventErrorCode;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@SuppressWarnings("PMD.NonStaticInitializer")
public class EventAPIProblemResponseMapper extends AbstractAPIProblemResponseMapper {

    {
        // configure mappers for post event
        ProblemResponseMappingOperation<EventErrorCode> postEventOp = new ProblemResponseMappingOperation
            .ProblemResponseMappingOperationBuilder<EventErrorCode>()
            .operation(EventErrorCode.class)
            .exception(this::createException).build();

        // create mappings
        postEventOp.addMapping(postEventOp.createProblemResponseMapping()
                                   .problem(EventErrorCode.EVENT_HANDLER_NOT_FOUND_IN_DB)
                                   .message(CodeAndMessage.NOT_FOUND_HANLDER).build());

        addOperationMappings(postEventOp);
    }

    private ClientProblemException createException(ProblemAndMapping problemAndMapping) {
        return new ClientProblemException(
            null,
            problemAndMapping.getProblem().getDetail(),
            problemAndMapping.getMapping().getMessage(),
            problemAndMapping.getProblem());
    }
}

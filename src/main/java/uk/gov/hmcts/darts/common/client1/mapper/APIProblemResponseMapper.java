package uk.gov.hmcts.darts.common.client1.mapper;

import uk.gov.hmcts.darts.common.client1.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;

import java.util.List;
import java.util.Optional;

public interface APIProblemResponseMapper {

    <T> void addOperationMappings(ProblemResponseMappingOperation<T> operation);

    Optional<ClientProblemException> getExceptionForProblem(Problem problem);

    Optional<ProblemResponseMapping<?>> getMapping(Problem problem);

    List<ProblemResponseMappingOperation> getResponseMappings();
}

package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.Optional;

public interface APIProblemResponseMapper {

    <T> void addMapper(Class<T> operation, ProblemResponseMapping<T> mapping);

    Optional<CodeAndMessage> getCodeAndMessage(Problem p);

    Optional<ClientProblemException> getExceptionForProblem(Problem p);
}

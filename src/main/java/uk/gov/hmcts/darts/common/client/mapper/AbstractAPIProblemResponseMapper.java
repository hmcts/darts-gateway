package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractAPIProblemResponseMapper implements APIProblemResponseMapper {
    private final List<ProblemResponseMappingOperation<?>> operationErrorResponseMappingList = Collections.synchronizedList(
        new ArrayList<>());

    @Override
    public <T> void addOperationMappings(ProblemResponseMappingOperation<T> operation) {
        operationErrorResponseMappingList.add(operation);
    }

    @Override
    public Optional<? extends ProblemResponseMapping<?>> getMapping(Problem problem) {
        for (ProblemResponseMappingOperation<?> operation : operationErrorResponseMappingList) {
            List<? extends ProblemResponseMapping<?>> mappingList = operation.getProblemResponseMappingList();
            Optional<? extends ProblemResponseMapping<?>> fnd = mappingList.stream().filter(m -> m.match(problem)).findFirst();

            if (fnd.isPresent()) {
                return fnd;
            }
        }

        return Optional.empty();
    }

    private Optional<? extends ProblemResponseMapping<?>> getMapping(ProblemResponseMappingOperation<?> operation, Problem problem) {
        List<? extends ProblemResponseMapping<?>> mappingList = operation.getProblemResponseMappingList();
        return mappingList.stream().filter(m -> m.match(problem)).findFirst();
    }

    private Optional<ProblemResponseMappingOperation<?>> getOperationForProblem(Problem problem) {
        for (ProblemResponseMappingOperation<?> operation : operationErrorResponseMappingList) {
            List<? extends ProblemResponseMapping<?>> mappingList = operation.getProblemResponseMappingList();
            Optional<? extends ProblemResponseMapping<?>> fnd = mappingList.stream().filter(m -> m.match(problem)).findFirst();
            if (fnd.isPresent()) {
                return Optional.of(operation);
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<ClientProblemException> getExceptionForProblem(Problem problem) {
        Optional<ProblemResponseMappingOperation<?>> operation = getOperationForProblem(problem);
        if (operation.isPresent()) {
            Optional<? extends ProblemResponseMapping<?>> mapping = getMapping(operation.get(), problem);
            ClientProblemException returnEx;

            if (mapping.isPresent()) {
                returnEx = operation.get().getException()
                        .apply(new ProblemAndMapping(problem, mapping.get()));
                return Optional.of(returnEx);
            }
        }
        return Optional.empty();
    }
}

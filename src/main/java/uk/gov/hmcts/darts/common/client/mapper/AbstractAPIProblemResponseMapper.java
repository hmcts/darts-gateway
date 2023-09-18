package uk.gov.hmcts.darts.common.client.mapper;

import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;
import uk.gov.hmcts.darts.model.audio.Problem;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractAPIProblemResponseMapper implements APIProblemResponseMapper {
    private final Map<Class<?>, List<ProblemResponseMapping<?>>> operationErrorResponseMappingList = new HashMap<>();

    public <T> void addMapper(Class<T> operation, ProblemResponseMapping<T> mapping) {
        if (!operationErrorResponseMappingList.containsKey(operation)) {
            List<ProblemResponseMapping<?>> mappingsLst = new ArrayList<>();
            operationErrorResponseMappingList.put(operation, mappingsLst);
        }
        operationErrorResponseMappingList.get(operation).add(mapping);
    }

    @Override
    public Optional<CodeAndMessage> getCodeAndMessage(Problem p) {

        for (Class<?> operation : operationErrorResponseMappingList.keySet()) {
            Optional<ProblemResponseMapping<?>> mapping = operationErrorResponseMappingList.get(operation).stream().filter(
                m -> m.match(
                    p)).findFirst();

            return mapping.isPresent() ? mapping.map(ProblemResponseMapping::getMessage) : Optional.empty();
        }

        return Optional.empty();
    }

    public Optional<ProblemResponseMapping<?>> getMapping(Problem p) {
        for (Class<?> operation : operationErrorResponseMappingList.keySet()) {
            List<ProblemResponseMapping<?>> mappingList = operationErrorResponseMappingList.get(operation);
            return mappingList.stream().filter(m -> m.match(p)).findFirst();
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    protected <T> Optional<ClientProblemException> getProblemValueForProblem(Class<T> operation, Problem p,
                                                                             Function<ProblemResponseMapping<T>,
                                                                                 ClientProblemException> exceptionSupplier) {
        List<ProblemResponseMapping<?>> mappingList = operationErrorResponseMappingList.get(operation);

        if (!mappingList.isEmpty()) {
            Optional<ProblemResponseMapping<?>> mapping = mappingList.stream().filter(m -> m.match(p)).findFirst();

            if (mapping.isPresent()) {
                ProblemResponseMapping<T> errMapping = (ProblemResponseMapping<T>) mapping.get();

                return Optional.of(exceptionSupplier.apply(errMapping));
            }
        }
        ;

        return Optional.empty();
    }
}

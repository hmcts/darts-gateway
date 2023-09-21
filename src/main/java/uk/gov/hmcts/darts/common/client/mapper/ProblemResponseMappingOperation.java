package uk.gov.hmcts.darts.common.client.mapper;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.darts.common.client.exeption.ClientProblemException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Builder
@Getter
public class ProblemResponseMappingOperation<T> {
    @Builder.Default
    private List<ProblemResponseMapping<T>> problemResponseMappingList = Collections.synchronizedList(
        new ArrayList<>());

    private Class<T> operation;
    private Function<ProblemAndMapping, ClientProblemException> exception;
    private ProblemResponseMapping.ProblemResponseMappingBuilder<T> builder;

    public ProblemResponseMappingOperation<T> addMapping(ProblemResponseMapping<T> mapping) {
        problemResponseMappingList.add(mapping);
        return this;
    }

    public ProblemResponseMapping.ProblemResponseMappingBuilder<T>  createProblemResponseMapping() {
        return new ProblemResponseMapping.ProblemResponseMappingBuilder<>();
    }
}

package uk.gov.hmcts.darts.common.client1.mapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.darts.model.audio.Problem;

@Getter
@RequiredArgsConstructor
public class ProblemAndMapping {
    private final Problem problem;

    private final ProblemResponseMapping<?> mapping;
}

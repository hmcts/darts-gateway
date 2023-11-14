package uk.gov.hmcts.darts.workflow.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@RequiredArgsConstructor
public class AddAudioIntegerTime {
    private final BigInteger hour;
    private final BigInteger minute;
    private final BigInteger second;
}

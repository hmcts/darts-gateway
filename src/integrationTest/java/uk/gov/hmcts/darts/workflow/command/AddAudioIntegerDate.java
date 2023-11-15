package uk.gov.hmcts.darts.workflow.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@RequiredArgsConstructor
public class AddAudioIntegerDate {
    private final BigInteger day;
    private final BigInteger month;
    private final BigInteger year;
}

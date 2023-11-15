package uk.gov.hmcts.darts.workflow.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AddAudioTime {
    private final String hour;
    private final String minute;
    private final String second;
}

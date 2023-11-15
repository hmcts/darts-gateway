package uk.gov.hmcts.darts.workflow.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AddAudioDate {
    private final String day;
    private final String month;
    private final String year;
}

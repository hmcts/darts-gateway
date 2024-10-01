package uk.gov.hmcts.darts.utils;

import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.Command;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

import java.io.IOException;

public final class MtomStagingClient {
    private static final int NUMBER_OF_FILE_TRAMSFERS = 1;

    private MtomStagingClient() {
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0;i < NUMBER_OF_FILE_TRAMSFERS; i++) {
            Command command = CommandFactory.getAudioCommand("darts-proxy.staging.platform.hmcts.net",
                                                             AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE);
            command.executeWithDocker(command.getArguments());
        }
    }
}
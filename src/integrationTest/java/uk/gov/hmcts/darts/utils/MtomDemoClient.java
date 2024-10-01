package uk.gov.hmcts.darts.utils;

import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.Command;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

import java.io.IOException;

public final class MtomDemoClient {
    private static final int NUMBER_OF_FILE_TRAMSFERS = 1;

    private MtomDemoClient() {
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0;i < NUMBER_OF_FILE_TRAMSFERS; i++) {
            Command command = CommandFactory.getAudioCommand("darts-proxy.demo.platform.hmcts.net",
                                                             AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE);
            command.executeWithDocker(command.getArguments());
        }
    }
}
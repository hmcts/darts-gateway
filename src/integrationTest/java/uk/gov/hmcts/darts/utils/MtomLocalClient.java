package uk.gov.hmcts.darts.utils;

import org.junit.jupiter.api.Assertions;
import uk.gov.hmcts.darts.testutils.IntegrationBase;
import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.Command;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

import java.io.IOException;

public final class MtomLocalClient {
    private static final int NUMBER_OF_FILE_TRAMSFERS = 1;

    private MtomLocalClient() {
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0;i < NUMBER_OF_FILE_TRAMSFERS; i++) {
            Command command = CommandFactory.getAudioCommand(IntegrationBase.getIp() + ":8070",
                                                             AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE);
            command.executeWithDocker(command.getArguments());
            Assertions.assertTrue(command.getLogOutput().contains("Code: 200"));
        }
    }
}
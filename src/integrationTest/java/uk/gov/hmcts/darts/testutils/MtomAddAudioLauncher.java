package uk.gov.hmcts.darts.testutils;

import uk.gov.hmcts.darts.workflow.command.AddAudioMidTierCommand;
import uk.gov.hmcts.darts.workflow.command.Command;
import uk.gov.hmcts.darts.workflow.command.CommandFactory;

import java.io.IOException;

public final class MtomAddAudioLauncher {
    private static final int NUMBER_OF_FILE_TRAMSFERS = 1;

    private static final int NUMBER_OF_ARGUMENTS = 3;

    private MtomAddAudioLauncher() {
    }


    /**
     * Requires three arguments to launch.
     * @param args - hostname of the gateway, username of the user making the call, password of the user making the call
     */
    @SuppressWarnings({"PMD.SystemPrintln"})
    public static void main(String[] args) throws IOException {
        for (int i = 0;i < NUMBER_OF_FILE_TRAMSFERS; i++) {

            if (args.length < NUMBER_OF_ARGUMENTS) {
                System.out.println("You need to supply the relevant user name and password");
            }

            Command command = CommandFactory.getAudioCommand(args[0],
                                                             AddAudioMidTierCommand.SAMPLE_XML, AddAudioMidTierCommand.SAMPLE_FILE, args[1], args[2]);
            command.executeWithDocker(command.getArguments());
        }
    }
}
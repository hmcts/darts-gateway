package uk.gov.hmcts.darts.workflow.command;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public class AddAudioMidTierCommand implements Command {

    private int exitCode;

    private final String ipToGateway;

    private final Audio audio;

    private final File fileToSend;

    private File tempFile;

    public static final File SAMPLE_FILE =
        new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/sample6.mp2").getFile());

    public static final Audio getDefaultAddAudioMetadata() throws IOException, JAXBException {
        File audioFile = new File(AddAudioMidTierCommand.class.getClassLoader().getResource(
            "addaudio/addAudio.xml").getFile());
        InputStream audioFileStream = Files.newInputStream(audioFile.toPath());

        JAXBContext jaxbContext = JAXBContext.newInstance(Audio.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Audio) unmarshaller.unmarshal(audioFileStream);
    }

    public AddAudioMidTierCommand(String ipaddress, Audio audio, File fileToSend) {
        audio.setMediafile(fileToSend.getName());
        this.ipToGateway = ipaddress;
        this.audio = audio;
        this.fileToSend = fileToSend;
        exitCode = 0;
    }

    @Override
    public void cleanupResources() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Override
    public void execute() {
        try {
            cleanupResources();
            tempFile = File.createTempFile("audio", ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Audio.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(audio, tempFile);

            File file =
                new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/addaudio.sh").getFile());

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(file.getAbsolutePath(), "http://" + ipToGateway + "/service/", tempFile.getAbsolutePath(), fileToSend.getAbsolutePath());
            Process process = builder.start();
            exitCode = process.exitValue();

            if (!isSuccess()) {
                throw new CommandException(exitCode, null);
            }
        } catch (IOException | JAXBException e) {
            throw new CommandException(exitCode, e);
        }
    }

    @Override
    public boolean isSuccess() {
        return exitCode == 0;
    }

    public Audio getAudioInput() {
        return audio;
    }
}

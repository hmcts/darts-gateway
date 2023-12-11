package uk.gov.hmcts.darts.workflow.command;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;
import uk.gov.hmcts.darts.config.SoapWebServiceConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public class AddAudioMidTierCommand implements Command {

    private int exitCode;

    private final String ipToGateway;

    private Audio audio;

    private final File fileToSend;

    private File audioFile;

    private File tempFile;

    public static final File SAMPLE_FILE =
            new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/sample6.mp2").getFile());

    public static final File SAMPLE_XML =
            new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/addAudio.xml").getFile());

    private GenericContainer container;

    public static final String BASE_DOCKER_COMMAND_WEB_CONTEXT = "/service";

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

    public AddAudioMidTierCommand(String ipaddress, File audioFile, File fileToSend) {
        this.ipToGateway = ipaddress;
        this.audioFile = audioFile;
        this.fileToSend = fileToSend;
        exitCode = 0;
    }

    @Override
    public void cleanupResources() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    private File getAudioFile() throws IOException {
        if (audio != null) {
            try {
                tempFile = File.createTempFile("audio", ".xml");
                JAXBContext jaxbContext = JAXBContext.newInstance(Audio.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.marshal(audio, tempFile);
                return tempFile;
            } catch (JAXBException jaxbException) {
                throw new IOException(jaxbException);
            }
        }

        return audioFile;
    }

    @Override
    public void execute() {
        try {
            File xmlFile = getAudioFile();

            File file =
                    new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/addaudio.sh").getFile());

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(file.getAbsolutePath(), "http://" + ipToGateway + SoapWebServiceConfig.BASE_WEB_CONTEXT, xmlFile.getAbsolutePath(), fileToSend.getAbsolutePath());
            Process process = builder.start();
            exitCode = process.exitValue();

            if (!isSuccess()) {
                throw new CommandException(exitCode, null);
            }
        } catch (IOException e) {
            throw new CommandException(exitCode, e);
        } finally {
            cleanupResources();
        }
    }

    @Override
    public void executeWithDocker() throws IOException {
        File xmlFile = getAudioFile();

        try (GenericContainer<?> container = getContainer(xmlFile)) {
            container.start();

            Container.ExecResult result = container.execInContainer(
                    "sh", "addaudio.sh",
                    "http://" + ipToGateway + BASE_DOCKER_COMMAND_WEB_CONTEXT,
                    xmlFile.getName(),
                    fileToSend.getName());

            exitCode = result.getExitCode();

            if (!isSuccess()) {
                throw new CommandException(exitCode, null);
            }
        } catch (IOException | InterruptedException e) {
            throw new CommandException(exitCode, e);
        } finally {
            cleanupResources();
        }
    }

    private GenericContainer getContainer(File xmlFile) {

        ImageFromDockerfile importDocker = new ImageFromDockerfile()
                .withDockerfileFromBuilder(builder ->
                        builder
                                .from("williamyeh/java8")
                                .cmd("tail", "-f", "/dev/null")
                                .build());
        if (container == null || !container.isRunning()) {
            container = new GenericContainer<>(importDocker)
                    .withCopyToContainer(MountableFile.forHostPath("./src/integrationTest/resources/addaudio", 077), "/")
                    .withCopyToContainer(MountableFile.forHostPath("./src/integrationTest/resources/addaudio/Lib", 777), "/")
                    .withCopyFileToContainer(MountableFile.forHostPath(fileToSend.getAbsolutePath(), 777), "/")
                    .withCopyFileToContainer(MountableFile.forHostPath(xmlFile.getAbsolutePath(), 777), "/");

        }

        return container;
    }

    @Override
    public boolean isSuccess() {
        return exitCode == 0;
    }

    public Audio getAudioInput() {
        return audio;
    }
}

package uk.gov.hmcts.darts.workflow.command;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.IndefiniteWaitOneShotStartupCheckStrategy;
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;


public class AddAudioMidTierCommand implements Command {

    private int exitCode;

    private final String ipToGateway;

    private Audio audio = null;

    private final File fileToSend;

    private File audioFile = null;

    private File tempFile;

    public static final File SAMPLE_FILE =
            new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/sample6.mp2").getFile());

    public static final File SAMPLE_XML =
            new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/addaudio.xml").getFile());

    private GenericContainer container;

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

    private File getAudioFile() throws Exception
    {
        if (audio!=null) {
            tempFile = File.createTempFile("audio", ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Audio.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(audio, tempFile);
            return tempFile;
        }

        return audioFile;
    }

    @Override
    public void execute() throws Exception {
        try {
            cleanupResources();

            File xmlFile = getAudioFile();

            File file =
                new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/addaudio.sh").getFile());

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(file.getAbsolutePath(), "http://" + ipToGateway + "/service/", xmlFile.getAbsolutePath(), fileToSend.getAbsolutePath());
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
    public void executeWithDocker() throws Exception{
        try {
            cleanupResources();

            File xmlFile = getAudioFile();
            container = getContainer(xmlFile);
            container.start();;

            Container.ExecResult result = container.execInContainer(
                    "sh", "addaudio.sh",
                    "http://" + ipToGateway + "/service/",
                    xmlFile.getName(),
                    fileToSend.getName());

            exitCode = result.getExitCode();

            if (!isSuccess()) {
                throw new CommandException(exitCode, null);
            }
        }
        catch (IOException | InterruptedException | JAXBException e) {
            throw new CommandException(exitCode, e);
        }
        finally {
            container.close();
        }
    }

    private GenericContainer getContainer(File xmlFile) throws Exception{

        ImageFromDockerfile importDocker = new ImageFromDockerfile()
                .withDockerfileFromBuilder(builder ->
                        builder
                                .from("williamyeh/java8")
                                .cmd("tail", "-f", "/dev/null")
                                .build());
        if (container == null || !container.isRunning()) {
            container = new GenericContainer<>(importDocker)
                    .withCopyToContainer(MountableFile.forHostPath("./src/integrationTest/resources/addaudio", 0777),  "/")
                    .withCopyToContainer(MountableFile.forHostPath("./src/integrationTest/resources/addaudio/Lib", 0777),  "/")
                    .withCopyFileToContainer(MountableFile.forHostPath(fileToSend.getAbsolutePath(), 0777),  "/")
                    .withCopyFileToContainer(MountableFile.forHostPath(xmlFile.getAbsolutePath(), 0777),  "/");

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

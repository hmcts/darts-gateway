package uk.gov.hmcts.darts.workflow.command;

import com.service.mojdarts.synapps.com.addaudio.Audio;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AddAudioMidTierCommand implements Command {

    public static final String BASE_WEB_CONTEXT = "/service/darts/";

    private int exitCode;

    private final String ipToGateway;

    private Audio audio;

    private final File fileToSend;

    private File audioFile;

    private File tempFile;

    private String logOutput;

    private final String username;

    private final String password;

    public static final File SAMPLE_FILE =
        new File(Thread.currentThread().getContextClassLoader().getResource("addaudio/sample6.mp2").getFile());

    public static final File SAMPLE_XML =
        new File(Thread.currentThread().getContextClassLoader().getResource("addaudio/addAudio.xml").getFile());

    public static final File BAD_SIGNATURE_FILE =
        new File(AddAudioMidTierCommand.class.getClassLoader().getResource("addaudio/badsignature.mp2").getFile());

    public static final File AUDIO_DIR = new File(Thread.currentThread().getContextClassLoader().getResource("addaudio").getFile());

    private GenericContainer container;

    public static final String BASE_DOCKER_COMMAND_WEB_CONTEXT = "/service";

    public static final Audio getDefaultAddAudioMetadata() throws IOException, JAXBException {
        File audioFile = new File(Thread.currentThread().getContextClassLoader().getResource(
            "addaudio/addAudio.xml").getFile());
        try (InputStream audioFileStream = Files.newInputStream(audioFile.toPath())) {

            JAXBContext jaxbContext = JAXBContext.newInstance(Audio.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (Audio) unmarshaller.unmarshal(audioFileStream);
        }
    }

    public AddAudioMidTierCommand(String ipaddress, Audio audio, File fileToSend, String username, String password) {
        audio.setMediafile(fileToSend.getName());
        this.ipToGateway = ipaddress;
        this.audio = audio;
        this.fileToSend = fileToSend;
        exitCode = 0;
        this.username = username;
        this.password = password;
    }

    public AddAudioMidTierCommand(String ipaddress, File audioFile, File fileToSend, String username, String password) {
        this.ipToGateway = ipaddress;
        this.audioFile = audioFile;
        this.fileToSend = fileToSend;
        exitCode = 0;
        this.username = username;
        this.password = password;
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
    public void execute(Map envVariables) {
        try {
            File xmlFile = getAudioFile();

            File file = new File(Thread.currentThread().getContextClassLoader().getResource("addaudio/addaudio.sh").getFile());

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(file.getAbsolutePath(), "http://" + ipToGateway + BASE_WEB_CONTEXT, xmlFile.getAbsolutePath(), fileToSend.getAbsolutePath(),username,password);
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
    public void executeWithDocker(Map envVariables) throws IOException {
        File xmlFile = getAudioFile();

        try (GenericContainer<?> container = getContainer(xmlFile)) {
            container.start();

            Container.ExecResult result;

            // invoke MTOM or UCF
            result = container.execInContainer(
                "sh", "addaudio.sh",
                "http://" + ipToGateway + BASE_DOCKER_COMMAND_WEB_CONTEXT,
                xmlFile.getName(),
                fileToSend.getName(),username, password);

            exitCode = result.getExitCode();


            logOutput = result.getStdout();
            log.info("Container output {} ", result.getStdout());
            log.info("Container output error {} ", result.getStderr());

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
                                               .from("hmctspublic.azurecr.io/imported/williamyeh/java8")
                                               .cmd("tail", "-f", "/dev/null")
                                               .build());
        if (container == null || !container.isRunning()) {
            container = new GenericContainer<>(importDocker)
                .withCopyToContainer(MountableFile.forHostPath(AUDIO_DIR.toPath(), 777), "/")
                .withCopyToContainer(MountableFile.forHostPath("./src/integrationTest/resources/addaudio", 777), "/")
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

    @Override
    public Map<String, String> getArguments() {
        return new HashMap<>();
    }

    @Override
    public Integer getPortForContainer() {
        return -1;
    }

    @Override
    public String getLogOutput() {
        return logOutput;
    }

    @Override
    public boolean isRunning() {
        return container.isRunning();
    }
}
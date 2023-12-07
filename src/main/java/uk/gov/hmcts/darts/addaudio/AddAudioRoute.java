package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.addaudio.Audio;
import com.synapps.moj.dfs.response.DARTSResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.common.client.AudiosClient;
import uk.gov.hmcts.darts.common.client.multipart.DefaultMultipart;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddAudioRoute {
    private final XmlParser xmlParser;
    private final AudiosClient audiosClient;
    private final AddAudioMapper addAudioMapper;
    private final XmlWithFileMultiPartRequestHolder multiPartRequestHolder;
    private final AddAudioValidator addAudioValidator;

    public DARTSResponse route(AddAudio addAudio) {
        addAudioValidator.validate(addAudio);

        var caseDocumentXmlStr = addAudio.getDocument();

        JAXBContext jaxbContext;
        Audio addAudioLegacy;

        try {
            jaxbContext = JAXBContext.newInstance(Audio.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            addAudioLegacy = (Audio) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(caseDocumentXmlStr.getBytes()));

            Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();

            // consume the uploaded file and proxy downstream
            request.get().consumeFileBinary(uploadedFile -> {
                DefaultMultipart multipartFile = new DefaultMultipart(
                    addAudioLegacy.getMediafile(),
                    addAudioLegacy.getMediaformat(),
                    uploadedFile
                );
                AddAudioMetadataRequest metaData = addAudioMapper.mapToDartsApi(addAudioLegacy);
                metaData.setFileSize(uploadedFile.length());
                audiosClient.addAudio(multipartFile, metaData);
            });
        } catch (JAXBException | IOException ioe) {
            throw new DartsValidationException(ioe, CodeAndMessage.ERROR);
        }

        CodeAndMessage mesage = CodeAndMessage.OK;
        return mesage.getResponse();
    }
}

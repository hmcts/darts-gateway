package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.addaudio.Audio;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.addaudio.validator.AddAudioValidator;
import uk.gov.hmcts.darts.common.client.AudioClient;
import uk.gov.hmcts.darts.common.client.multipart.DefaultMultipart;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequest;
import uk.gov.hmcts.darts.common.multipart.XmlWithFileMultiPartRequestHolder;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.ws.CodeAndMessage;
import uk.gov.hmcts.darts.ws.DartsException;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddAudioRoute {
    private final XmlParser xmlParser;
    private final AudioClient audiosClient;
    private final AddAudioMapper addAudioMapper;
    private final XmlWithFileMultiPartRequestHolder multiPartRequestHolder;
    private final AddAudioValidator addAudioValidator;

    public DARTSResponse route(AddAudio addAudio) {
        addAudioValidator.validate(addAudio);

        var audioXml = addAudio.getDocument();

        Audio addAudioLegacy;

        try {
            addAudioLegacy = (Audio) xmlParser.unmarshal(audioXml, Audio.class);

            Optional<XmlWithFileMultiPartRequest> request = multiPartRequestHolder.getRequest();

            if (request.isPresent()) {
                // consume the uploaded file and proxy downstream
                request.get().consumeFileBinaryStream(uploadedStream -> {
                    DefaultMultipart multipartFile = new DefaultMultipart(
                        addAudioLegacy.getMediafile(),
                        addAudioLegacy.getMediaformat(),
                        uploadedStream
                    );
                    AddAudioMetadataRequest metaData = addAudioMapper.mapToDartsApi(addAudioLegacy);
                    metaData.setFileSize(request.get().getBinarySize());
                    audiosClient.addAudioStream(multipartFile, metaData);
                });
            }
        } catch (IOException ioe) {
            throw new DartsException(ioe, CodeAndMessage.ERROR);
        }

        CodeAndMessage mesage = CodeAndMessage.OK;
        return mesage.getResponse();
    }
}

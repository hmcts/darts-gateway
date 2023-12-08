package uk.gov.hmcts.darts.addaudio;

import com.service.mojdarts.synapps.com.AddAudio;
import com.service.mojdarts.synapps.com.addaudio.Audio;
import com.synapps.moj.dfs.response.DARTSResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.darts.common.client.AudiosClient;
import uk.gov.hmcts.darts.utilities.XmlParser;
import uk.gov.hmcts.darts.utilities.XmlValidator;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

@Service
@RequiredArgsConstructor
public class AddAudioRoute {
    @Value("${darts-gateway.add-audio.schema}")
    private String addAudioSchemaPath;
    @Value("${darts-gateway.add-audio.validate}")
    private boolean validateAddAudio;

    private final XmlValidator xmlValidator;
    private final XmlParser xmlParser;

    private final AudiosClient audiosClient;
    private final AddAudioMapper addAudioMapper;

    public DARTSResponse route(AddAudio addAudio) {
        var addAudioDocumentXmlStr = addAudio.getDocument();
        if (validateAddAudio) {
            xmlValidator.validate(addAudioDocumentXmlStr, addAudioSchemaPath);
        }

        var addAudioLegacy = xmlParser.unmarshal(addAudioDocumentXmlStr, Audio.class);

        audiosClient.addAudio(null, addAudioMapper.mapToDartsApi(addAudioLegacy));

        CodeAndMessage okResponse = CodeAndMessage.OK;
        return okResponse.getResponse();
    }
}

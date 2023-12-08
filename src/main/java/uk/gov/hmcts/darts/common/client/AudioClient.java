package uk.gov.hmcts.darts.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class AudioClient extends AbstractFileUpload {
    @Value("${darts-gateway.darts-api.base-url}")
    private String url;

    private final DartsClientProblemDecoder decoder;

    private final RestTemplate template;

    /**
     * Add an audio using streaming.
     * @param multipartFile The multipart audio file to transfer. A default one that disableS in memory loading
     *                      can be found {@link uk.gov.hmcts.darts.common.client.multipart.DefaultMultipart}
     * @param audio The audio meta data
     */
    public void streamAudio(MultipartFile multipartFile, AddAudioMetadataRequest audio) {
        streamFileWithMetaData(multipartFile, audio, url + "/audios");
    }

    @Override
    protected RestTemplate getTemplate() {
        return template;
    }

    @Override
    protected DartsClientProblemDecoder getProblemDecoder() {
        return decoder;
    }
}

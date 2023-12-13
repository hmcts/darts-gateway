package uk.gov.hmcts.darts.common.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.api.audio.AudiosApi;
import uk.gov.hmcts.darts.common.client.component.HttpHeadersInterceptor;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;

import java.util.List;


@Slf4j
@Component
public class AudiosClient extends AbstractRestTemplateClient implements AudiosApi {
    @Value("${darts-gateway.darts-api.baseUrl}")
    private String url;

    private final DartsClientProblemDecoder decoder;

    private final RestTemplate template;

    public AudiosClient(List<HttpHeadersInterceptor> interceptors, DartsClientProblemDecoder decoder, RestTemplate template) {
        super(interceptors);
        this.decoder = decoder;
        this.template = template;
    }

    /**
     * Add an audio using streaming.
     * @param multipartFile The multipart audio file to transfer. A default one that disableS in memory loading
     *                      can be found {@link uk.gov.hmcts.darts.common.client.multipart.StreamingMultipart}
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

    @Override
    public ResponseEntity<Void> addAudio(MultipartFile file, AddAudioMetadataRequest metadata) {
        streamAudio(file, metadata);
        return new ResponseEntity<Void>(HttpStatusCode.valueOf(200));
    }
}

package uk.gov.hmcts.darts.common.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.api.audio.AudiosApi;
import uk.gov.hmcts.darts.common.client.component.HttpHeadersInterceptor;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequestWithStorageGUID;

import java.util.List;


@Slf4j
@Component
public class AudiosClient extends AbstractRestTemplateClient implements AudiosApi {
    @Value("${darts-gateway.darts-api.base-url}")
    private String baseUrl;

    private final DartsClientProblemDecoder decoder;

    private final RestTemplate template;
    private final LogApi logApi;

    public AudiosClient(List<HttpHeadersInterceptor> interceptors, DartsClientProblemDecoder decoder, RestTemplate template,
                        LogApi logApi) {
        super(interceptors);
        this.decoder = decoder;
        this.template = template;
        this.logApi = logApi;
    }

    /**
     * Add an audio using streaming.
     *
     * @param multipartFile The multipart audio file to transfer. A default one that disableS in memory loading
     *                      can be found {@link uk.gov.hmcts.darts.common.client.multipart.StreamingMultipart}
     * @param audio         The audio meta data
     */
    public void streamAudio(MultipartFile multipartFile, AddAudioMetadataRequest audio) {
        streamFileWithMetaData(multipartFile, audio, baseUrl + "/audios");
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
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @Override
    @SuppressWarnings("PMD.LooseCoupling")
    public ResponseEntity<Void> addAudioMetaData(AddAudioMetadataRequestWithStorageGUID addAudioMetadataRequestWithStorageGuid) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of());
            HttpEntity<AddAudioMetadataRequestWithStorageGUID> requestEntity = new HttpEntity<>(addAudioMetadataRequestWithStorageGuid, headers);
            processHttpHeaderInterceptors(headers);
            return getTemplate().postForEntity(baseUrl + "/audios/metadata", requestEntity, Void.class);
        } catch (HttpStatusCodeException e) {
            logApi.failedToLinkAudioToCases(
                addAudioMetadataRequestWithStorageGuid.getCourthouse(),
                addAudioMetadataRequestWithStorageGuid.getCourtroom(),
                addAudioMetadataRequestWithStorageGuid.getStartedAt(),
                addAudioMetadataRequestWithStorageGuid.getEndedAt(),
                addAudioMetadataRequestWithStorageGuid.getCases(),
                addAudioMetadataRequestWithStorageGuid.getChecksum(),
                addAudioMetadataRequestWithStorageGuid.getStorageGuid()
            );
            log.error("Darts api client exception", e);
            throw getProblemDecoder().decode(e);
        }
    }
}

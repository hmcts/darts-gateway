package uk.gov.hmcts.darts.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;
import uk.gov.hmcts.darts.model.audio.AddAudioMetadataRequest;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class AudioClient {
    @Value("${darts-gateway.darts-api.base-url}")
    private String url;

    private final DartsClientProblemDecoder decoder;

    private final RestTemplate template;

    public void addAudioStream(MultipartFile multipart, AddAudioMetadataRequest audio) throws IOException {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            map.put("metadata", Arrays.asList(audio));
            map.put("file", Arrays.asList(multipart.getResource()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
            template.postForEntity(url + "/audios", requestEntity, String.class);
        } catch (HttpStatusCodeException e) {
            log.error("Darts api client exception", e);
            throw decoder.decode(e);
        }
    }
}

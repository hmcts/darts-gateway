package uk.gov.hmcts.darts.common.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.darts.common.client.exeption.DartsClientProblemDecoder;

import java.util.Collections;
import java.util.List;

/**
 * Any class that uploads files MUST extend this class. Do not use feign directly it will store files in memory before proxying
 */
@Slf4j
public abstract class AbstractFileUpload {
    /**
     * Uploads a file by streaming NOTE: This mechanism does not use feign for the reason that feign loads files into memory (see
     * https://github.com/OpenFeign/feign-form/issues/88)
     * @param multipartFile The multipart file to transfer. A default one that disableS in memory loading
     *                      can be found {@link uk.gov.hmcts.darts.common.client.multipart.DefaultMultipart}
     * @param metadata The meta data
     * @param url The url to use
     */
    public <T> void streamFileWithMetaData(MultipartFile multipartFile, T metadata, String url) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            map.put("metadata", Collections.singletonList(metadata));
            map.put("file", List.of(multipartFile.getResource()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
            getTemplate().postForEntity(url, requestEntity, String.class);
        } catch (HttpStatusCodeException e) {
            log.error("Darts api client exception", e);
            throw getProblemDecoder().decode(e);
        }
    }

    /**
     * The template to send the request.
     * @return The template
     */
    protected abstract RestTemplate getTemplate();


    /**
     * handles the error. This is the feign {@link feign.codec.ErrorDecoder} equivalent
     * @return The decoder
     */
    protected abstract DartsClientProblemDecoder getProblemDecoder();
}

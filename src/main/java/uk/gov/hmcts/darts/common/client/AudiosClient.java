package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.audio.AudiosApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "audios", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface AudiosClient extends AudiosApi {
}

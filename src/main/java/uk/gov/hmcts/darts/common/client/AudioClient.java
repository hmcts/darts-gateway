package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.audioRequest.AudioApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "audio", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface AudioClient extends AudioApi {
}

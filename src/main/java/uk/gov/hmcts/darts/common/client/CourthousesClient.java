package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.courthouses.CourthousesApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "courthouses", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface CourthousesClient extends CourthousesApi {
}

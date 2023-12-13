package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.event.EventsApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "events", url = "${darts-gateway.darts-api.baseUrl}", configuration = ServiceConfig.class)
public interface EventsClient extends EventsApi {
}

package uk.gov.hmcts.darts.event.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.darts.config.FeignConfig;
import uk.gov.hmcts.darts.event.model.EventRequest;
import uk.gov.hmcts.darts.event.model.EventResponse;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "eventApiClient", url = "${darts-gateway.darts-api.events}", configuration = FeignConfig.class)
public interface EventApiClient {

    @RequestMapping(method = POST)
    @Headers("Content-Type: application/json")
    EventResponse sendEvent(@RequestBody EventRequest eventRequest);

}

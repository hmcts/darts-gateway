package uk.gov.hmcts.darts.events;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.hmcts.darts.config.FeignConfig;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "eventApiClient", url = "${darts-gateway.darts-api.events}", configuration = FeignConfig.class)
public interface EventApiClient {

    @RequestMapping(method = POST)
    @Headers("Content-Type: application/json")
    EventResponse sendEvent(@RequestBody EventRequest eventRequest);

}

package uk.gov.hmcts.darts.events;

import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "eventApiClient", url = "http://localhost:4550")
public interface EventApiClient {

    @RequestMapping(method = RequestMethod.POST, value = "/events/free-text", produces = "application/json")
    void sendEvent(@Param("eventRequest") EventRequest eventRequest);

}

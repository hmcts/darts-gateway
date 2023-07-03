package uk.gov.hmcts.darts.common.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.darts.config.FeignConfig;
import uk.gov.hmcts.darts.event.model.EventRequest;
import uk.gov.hmcts.darts.event.model.EventResponse;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "dartsFeignClient", url = "${darts-gateway.darts-api.base-url}", configuration = FeignConfig.class)
public interface DartsFeignClient {

    @RequestMapping(method = GET, value = "/cases")
    @Headers("Content-Type: application/json")
    List<ScheduledCase> getCases(@RequestParam("courthouse") String courthouse,
                                 @RequestParam("courtroom") String courtroom,
                                 @RequestParam("date") String date
    );

    @RequestMapping(method = POST, value = "/events")
    @Headers("Content-Type: application/json")
    EventResponse sendEvent(@RequestBody EventRequest eventRequest);

}

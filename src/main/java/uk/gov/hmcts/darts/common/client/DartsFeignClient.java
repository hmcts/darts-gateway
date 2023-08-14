package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.darts.config.FeignConfig;
import uk.gov.hmcts.darts.event.model.EventRequest;
import uk.gov.hmcts.darts.event.model.EventResponse;
import uk.gov.hmcts.darts.model.cases.AddCaseRequest;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.model.events.CourtLog;
import uk.gov.hmcts.darts.model.dailyList.DailyList;
import uk.gov.hmcts.darts.model.events.CourtLogsPostRequestBody;
import uk.gov.hmcts.darts.model.dailyList.DailyList;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "dartsFeignClient", url = "${darts-gateway.darts-api.base-url}", configuration = FeignConfig.class)
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public interface DartsFeignClient {

    @GetMapping(value = "/cases", headers = "accept=application/json")
    List<ScheduledCase> getCases(@RequestParam("courthouse") String courthouse,
                                 @RequestParam("courtroom") String courtroom,
                                 @RequestParam("date") String date
    );

    @PostMapping(value = "/cases", headers = {"accept=application/json", "Content-Type=application/json"})
    void addCase(@RequestBody AddCaseRequest addCaseRequest);

    @PostMapping(value = "/events", headers =  {"accept=application/json", "Content-Type=application/json"})
    EventResponse sendEvent(@RequestBody EventRequest eventRequest);

    @GetMapping(value = "/courtlogs", headers =  "accept=application/json")
    List<CourtLog> getCourtLogs(@RequestParam("courthouse") String courthouse,
                                @RequestParam("case_number") String caseNumber,
                                @RequestParam("start_date_time") String startDateTime,
                                @RequestParam("end_date_time") String endDateTime);

    @RequestMapping(method = POST, value = "/courtlogs", headers = {"accept=application/json", "Content-Type=application/json"})
    EventResponse postCourtLogs(@RequestBody CourtLogsPostRequestBody requestBody);

    @RequestMapping(method = POST, value = "/dailylists", headers = "accept=application/json")
    void postDailyLists(@RequestParam("source_system") String sourceSystem,
                                @RequestParam("DailyList") DailyList dailyList);

}

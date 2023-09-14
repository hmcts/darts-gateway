package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.darts.config.FeignConfig;
import uk.gov.hmcts.darts.event.model.EventRequest;
import uk.gov.hmcts.darts.event.model.EventResponse;
import uk.gov.hmcts.darts.model.cases.AddCaseRequest;
import uk.gov.hmcts.darts.model.cases.ScheduledCase;
import uk.gov.hmcts.darts.model.dailylist.PostDailyListResponse;
import uk.gov.hmcts.darts.model.event.CourtLog;
import uk.gov.hmcts.darts.model.event.CourtLogsPostRequestBody;

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

    @PostMapping(value = "/events", headers = {"accept=application/json", "Content-Type=application/json"})
    EventResponse sendEvent(@RequestBody EventRequest eventRequest);

    @GetMapping(value = "/courtlogs", headers = "accept=application/json")
    List<CourtLog> getCourtLogs(@RequestParam("courthouse") String courthouse,
                                @RequestParam("case_number") String caseNumber,
                                @RequestParam("start_date_time") String startDateTime,
                                @RequestParam("end_date_time") String endDateTime);

    @RequestMapping(method = POST, value = "/courtlogs", headers = {"accept=application/json", "Content-Type=application/json"})
    EventResponse postCourtLogs(@RequestBody CourtLogsPostRequestBody requestBody);

    @PostMapping(value = "/dailylists", headers = "accept=application/json")
    PostDailyListResponse postDailyLists(@RequestParam("source_system") String sourceSystem,
                                                                                            @RequestParam("courthouse") String courthouse,
                                                                                            @RequestParam("hearing_date") String hearingDate,
                                                                                            @RequestParam("unique_id") String uniqueId,
                                                                                            @RequestParam("published_ts") String publishedTs,
                                                                                            @RequestHeader("xml_document") String dailyListXml);

    @PatchMapping(value = "/dailylists", headers = "accept=application/json")
    PostDailyListResponse patchDailyLists(@RequestParam("dal_id") Integer dalId,
                                        @RequestHeader("json_document") String jsonDocument);

}

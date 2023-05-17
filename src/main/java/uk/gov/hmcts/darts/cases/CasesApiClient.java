package uk.gov.hmcts.darts.cases;

import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "casesApiClient", url = "http://localhost:4550")
public interface CasesApiClient {

    @RequestMapping(method = RequestMethod.POST, value = "/cases", produces = "application/json")
    void addCase(@Param("addCaseRequest") AddCaseRequest addCaseRequest);

}


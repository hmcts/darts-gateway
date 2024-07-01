package uk.gov.hmcts.darts.common.client1;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.cases.CasesApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "cases", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface CasesClient extends CasesApi {
}

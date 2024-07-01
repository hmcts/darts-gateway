package uk.gov.hmcts.darts.common.client1;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.event.CourtlogsApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "courtLogs", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface CourtLogsClient extends CourtlogsApi {
}

package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.dailyList.DailylistsApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "dailylists", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface DailyListsClient extends DailylistsApi {
}

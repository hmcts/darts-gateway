package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.audit.AuditApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "audit", url = "${darts-gateway.darts-api.base-url}", configuration = ServiceConfig.class)
public interface AuditClient extends AuditApi {
}

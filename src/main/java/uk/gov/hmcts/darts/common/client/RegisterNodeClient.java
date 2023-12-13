package uk.gov.hmcts.darts.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import uk.gov.hmcts.darts.api.noderegistration.RegisterDevicesApi;
import uk.gov.hmcts.darts.config.ServiceConfig;

@FeignClient(name = "register-devices", url = "${darts-gateway.darts-api.baseUrl}", configuration = ServiceConfig.class)
public interface RegisterNodeClient extends RegisterDevicesApi {
}

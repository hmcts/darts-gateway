package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.testutils.IntegrationBase;

@ActiveProfiles("int-test-jwt-token-shared")

class WsdlTest extends IntegrationBase {

    @Autowired
    private RestTemplate template;

    @Test
    void testDartsWsdlSuccess() throws Exception {
        String content = template.getForObject(getGatewayUri() + "/DARTSService?wsdl", String.class);
        Assertions.assertNotNull(content);
    }

    @Test
    void testContextRegistryWsdlSuccess() throws Exception {
        String content = template.getForObject(
            getGatewayUri() + "/runtime/ContextRegistryService?wsdl", String.class);
        Assertions.assertNotNull(content);
    }

    @Test
    void testContextRegistryWsdlSuccessWithoutQueryParamFail() throws Exception {
        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> {
            template.getForObject(
                getGatewayUri() + "/runtime/ContextRegistryService", String.class);
        });
    }

    @Test
    void testWsdlFail() {
        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> {
            template.getForObject(getGatewayUri() + "/DARTSServiceUnknown?wsdl", String.class);
        });
    }
}

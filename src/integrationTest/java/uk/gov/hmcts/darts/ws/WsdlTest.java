package uk.gov.hmcts.darts.ws;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.darts.utils.IntegrationBase;

@ActiveProfiles("int-test-jwt-token-shared")
class WsdlTest extends IntegrationBase {

    @Autowired
    private RestTemplate template;

    @Test
    public void testDartsWsdlSuccess() throws Exception {
        String content = template.getForObject(getGatewayUri() + "/DARTSService?wsdl", String.class);
        Assertions.assertNotNull(content);
    }

    @Test
    public void testContextRegistryWsdlSuccess() throws Exception {
        String content = template.getForObject(
            getGatewayUri() + "/runtime/ContextRegistryService?wsdl", String.class);
        Assertions.assertNotNull(content);
    }

    @Test
    public void testWsdlFail() {
        Assertions.assertThrows(HttpServerErrorException.InternalServerError.class, () -> {
            template.getForObject(getGatewayUri() + "/DARTSServiceUnknown?wsdl", String.class);
        });
    }
}

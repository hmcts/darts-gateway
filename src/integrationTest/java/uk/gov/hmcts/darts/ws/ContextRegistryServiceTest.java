package uk.gov.hmcts.darts.ws;

import documentum.contextreg.RegisterResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClientProvider;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

class ContextRegistryServiceTest extends IntegrationBase {
    @Test
    void testGetContextRegistryWsdl() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(getGatewayUri() + "ContextRegistryService?wsdl"))
            .timeout(Duration.ofMinutes(2))
            .header("Content-Type", "application/json")
            .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse.BodyHandler<?> responseBodyHandler = BodyHandlers.ofString();
        HttpResponse<?> response = client.send(request, responseBodyHandler);
        Assertions.assertFalse(response.body().toString().isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleRegister(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        String header = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleLookup(ContextRegistryClient client) throws Exception {
        Assertions.assertTrue(true);
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleUnregister(ContextRegistryClient client) throws Exception {
        Assertions.assertTrue(true);
    }
}

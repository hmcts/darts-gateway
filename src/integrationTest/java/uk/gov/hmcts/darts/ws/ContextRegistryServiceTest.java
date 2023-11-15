package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
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
            .uri(new URI(getGatewayUri() + "runtime/ContextRegistryService?wsdl"))
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

        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());
    }

    private String registerToken(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        String header = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        return response.getResponse().getValue().getReturn();
    }

    private LookupResponse lookup(ContextRegistryClient client) throws Exception {
        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        return response.getResponse().getValue();
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleUnregister(ContextRegistryClient client) throws Exception {

        String token = registerToken(client);
        Assertions.assertNotNull(lookup(client));

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/unregister/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/unregister/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<UnregisterResponse> response = client.unregister(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse());
        Assertions.assertNull(lookup(client).getReturn().getToken());
        Assertions.assertTrue(true);
    }
}

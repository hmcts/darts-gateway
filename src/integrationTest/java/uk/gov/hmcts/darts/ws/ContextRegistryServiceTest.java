package uk.gov.hmcts.darts.ws;

import com.ibm.icu.impl.Assert;
import contextreg.RegisterResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SOAPAssertionUtil;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClientProvider;
import uk.gov.hmcts.darts.utils.client.darts.DartsGatewayClient;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ContextRegistryServiceTest extends IntegrationBase {
    @Test
    public void testGetContextRegistryWsdl() throws Exception
    {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(getGatewayUri() + "ContextRegistryService?wsdl"))
            .timeout(Duration.ofMinutes(2))
            .header("Content-Type", "application/json")
            .build();
        HttpClient client = HttpClient.newBuilder().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleRegister(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/expectedResponse.xml");

        SOAPAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());
    }

    //@ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleLookup(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/getCases/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/getCases/dartsApiResponse.json");

        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/getCases/expectedResponse.xml");
    }
    //@ParameterizedTest
    @ArgumentsSource(ContextRegistryClientProvider.class)
    void handleUnregister(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/getCases/soapRequest.xml");

        String dartsApiResponseStr = TestUtils.getContentsFromFile(
            "payloads/getCases/dartsApiResponse.json");

        String expectedResponseStr = TestUtils.getContentsFromFile(
            "payloads/getCases/expectedResponse.xml");
    }
}

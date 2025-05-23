package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.testutils.CacheUtil;
import uk.gov.hmcts.darts.testutils.IntegrationBase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings("PMD.DoNotUseThreads")
public class ContextRegistryParent extends IntegrationBase {

    public static final String SERVICE_CONTEXT_USER = "user";

    public static final String SERVICE_CONTEXT_PASSWORD = "pass";

    void executeTestGetContextRegistryWsdl() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(getGatewayUri() + "runtime/ContextRegistryService?wsdl"))
            .timeout(Duration.ofMinutes(2))
            .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse.BodyHandler<?> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<?> response = client.send(request, responseBodyHandler);
        Assertions.assertNotNull(response.body());
    }

    String executeHandleRegisterMissingIdentity(ContextRegistryClient client) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequestNoIdentities.xml");

        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());

        return response.getResponse().getValue().getReturn();
    }

    String executeHandleRegisterMissingServiceContext(ContextRegistryClient client) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequestNoServiceContext.xml");

        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());

        return response.getResponse().getValue().getReturn();
    }

    String executeHandleRegister(ContextRegistryClient client) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", SERVICE_CONTEXT_USER);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", SERVICE_CONTEXT_PASSWORD);

        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());

        return response.getResponse().getValue().getReturn();
    }

    String executeHandleRegisterInvalidIdentity(ContextRegistryClient client) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", SERVICE_CONTEXT_USER);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", "");

        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());

        return response.getResponse().getValue().getReturn();
    }

    void executeHandleRegisterWithSharing(ContextRegistryClient client) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", SERVICE_CONTEXT_USER);
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", SERVICE_CONTEXT_PASSWORD);

        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());
        SoapAssertionUtil<RegisterResponse> response1 = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        SoapAssertionUtil<RegisterResponse> response2 = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        SoapAssertionUtil<RegisterResponse> response3 = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);

        Assertions.assertNotNull(response.getResponse().getValue());
        Assertions.assertEquals(response.getResponse().getValue().getReturn(), response1.getResponse().getValue().getReturn());
        Assertions.assertEquals(response.getResponse().getValue().getReturn(), response2.getResponse().getValue().getReturn());
        Assertions.assertEquals(response.getResponse().getValue().getReturn(), response3.getResponse().getValue().getReturn());
    }

    void executeHandleLookup(ContextRegistryClient client) throws JAXBException, IOException {

        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());
    }

    void executeHandleLookupNoIdentities(ContextRegistryClient client) throws IOException, JAXBException {

        String token = registerToken(getGatewayUri(), client, null, null);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());
    }

    void executeHandleLookupForToken(ContextRegistryClient client, String token) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());
    }

    void executeTestTimeToLive(ContextRegistryClient client, CacheProperties properties) throws JAXBException, IOException, InterruptedException {
        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());

        // we need to sleep duration for 10 seconds of inactivity. Token should disappear
        Thread.sleep(CacheUtil.getMillisForTimeToIdle(properties));
        response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNull(response.getResponse().getValue().getReturn());
    }

    void executeTestTimeToIdle(ContextRegistryClient client, CacheProperties properties) throws IOException, JAXBException, InterruptedException {
        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());

        int backAThird = (int) properties.getEntryTimeToIdleSeconds() / 3;
        // take us to seconds before expiry
        Thread.sleep(CacheUtil.getMillisForTimeToIdleMinusSeconds(properties, backAThird));

        // lookup to reset the timeout
        response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());

        // wait seconds which should take us past the original time to live period thereby provide that the last
        // lookup reset the timeout
        Thread.sleep(backAThird + CacheUtil.getMillisForSeconds(5));

        response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());

        // lets wait for the timeout in which case we will return null
        Thread.sleep(CacheUtil.getMillisForTimeToIdle(properties) + CacheUtil.getMillisForSeconds(5));
        response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNull(response.getResponse().getValue().getReturn());
    }

    protected String registerToken(ContextRegistryClient client) throws IOException, JAXBException {
        return registerToken(getGatewayUri(), client, SERVICE_CONTEXT_USER, SERVICE_CONTEXT_PASSWORD);
    }

    public static String registerToken(URL baseUrl, ContextRegistryClient client, String username, String password) throws IOException, JAXBException {
        String soapRequestStr;
        if (username == null && password == null) {
            soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/soapRequestNoIdentities.xml");
        } else {
            soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/soapRequest.xml");

            soapRequestStr = soapRequestStr.replace("${USER}", username);
            soapRequestStr = soapRequestStr.replace("${PASSWORD}", password);
        }

        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(baseUrl + "ContextRegistryService?wsdl"), soapRequestStr);
        return response.getResponse().getValue().getReturn();
    }


    @SuppressWarnings({"PMD.SimplifiableTestAssertion", "PMD.DoNotUseThreads", "PMD.AvoidInstantiatingObjectsInLoops"})
    void executeBasicConcurrency(ContextRegistryClient client, int usersCount, CacheProperties properties) throws InterruptedException {
        CountDownLatch semaphore = new CountDownLatch(usersCount);
        CountDownLatch lookupSemaphore = new CountDownLatch(usersCount);

        List<ContextRegistryParent.RegisterThread> registerThreadLst = new ArrayList<>();
        List<ContextRegistryParent.LookupThread> lookupThreadLst = new ArrayList<>();

        for (int i = 0; i < usersCount; i++) {
            ContextRegistryParent.RegisterThread thread = new ContextRegistryParent.RegisterThread(client, "user" + i, semaphore, properties);
            registerThreadLst.add(thread);
            new Thread(thread).start();
        }

        semaphore.await(1, TimeUnit.MINUTES);

        // lets take the threads to nearly expiration just to ensure we get lookup values back correctly
        Thread.sleep(CacheUtil.getMillisForTimeToIdle(properties));

        for (int i = 0; i < usersCount; i++) {
            ContextRegistryParent.LookupThread thread =
                new ContextRegistryParent.LookupThread(client, registerThreadLst.get(i).getToken(), lookupSemaphore, properties);
            lookupThreadLst.add(thread);
            new Thread(thread).start();
        }

        lookupSemaphore.await(1, TimeUnit.MINUTES);

        Assertions.assertFalse(registerThreadLst.isEmpty());
        Assertions.assertFalse(lookupThreadLst.isEmpty());

        for (ContextRegistryParent.RegisterThread thread : registerThreadLst) {
            Assertions.assertNotNull(thread.getToken());
        }

        for (ContextRegistryParent.LookupThread thread : lookupThreadLst) {
            Assertions.assertNotNull(thread.getValue());
        }
    }

    void executeTestHandleUnregister(ContextRegistryClient client) throws IOException, JAXBException {
        String token = registerToken(client);
        lookup(client, token);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/unregister/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<UnregisterResponse> response = client.unregister(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse());

        soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        Assertions.assertNull(client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr).getResponse().getValue().getReturn());
    }

    void executeTestHandleUnregisterSharing(ContextRegistryClient client) throws IOException, JAXBException {
        String token = registerToken(client);
        lookup(client, token);

        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/unregister/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        SoapAssertionUtil<UnregisterResponse> response = client.unregister(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse());

        soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        Assertions.assertNotNull(client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr).getResponse().getValue().getReturn());
    }

    private LookupResponse lookup(ContextRegistryClient client, String token) throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", StringUtils.trimToEmpty(token));

        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        return response.getResponse().getValue();
    }

    @Getter
    class RegisterThread implements Runnable {

        private final ContextRegistryClient client;

        private String token;

        private final String user;

        private final CountDownLatch latch;

        private final CacheProperties properties;

        RegisterThread(ContextRegistryClient client, String user, CountDownLatch latch, CacheProperties properties) {
            this.client = client;
            this.user = user;
            this.latch = latch;
            this.properties = properties;
        }

        @Override
        public void run() {
            try {
                token = ContextRegistryParent.this.registerToken(client);
                latch.countDown();
            } catch (Exception e) {
                throw new AuthenticationFailedException("The register operation errored", e);
            }
        }
    }

    @Getter
    class LookupThread implements Runnable {

        private final ContextRegistryClient client;

        private LookupResponse value;

        private final String token;

        private final CountDownLatch latch;

        private final CacheProperties properties;

        LookupThread(ContextRegistryClient client, String token, CountDownLatch latch, CacheProperties properties) {
            this.client = client;
            this.token = token;
            this.latch = latch;
            this.properties = properties;
        }

        @Override
        public void run() {
            try {
                value = ContextRegistryParent.this.lookup(client, token);
                latch.countDown();
            } catch (Exception e) {
                throw new AuthenticationFailedException("The lookup operation errored", e);
            }
        }
    }
}

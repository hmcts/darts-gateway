package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import junit.framework.AssertionFailedError;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;
import uk.gov.hmcts.darts.utils.CacheUtil;
import uk.gov.hmcts.darts.utils.IntegrationBase;
import uk.gov.hmcts.darts.utils.TestUtils;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ContextRegistryParent extends IntegrationBase {

    void executeTestGetContextRegistryWsdl() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(getGatewayUri() + "runtime/ContextRegistryService?wsdl"))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse.BodyHandler<?> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<?> response = client.send(request, responseBodyHandler);
        Assertions.assertNotNull(response.body());
    }

    void executeHandleRegister(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/soapRequest.xml");

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/requestHeaders.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", "user1");

        client.setHeaderBlock(header);
        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());
    }

    void executeHandleRegisterWithSharing(ContextRegistryClient client) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/soapRequest.xml");

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/requestHeaders.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", "user1");

        client.setHeaderBlock(header);
        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        SoapAssertionUtil<RegisterResponse> response1 = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        SoapAssertionUtil<RegisterResponse> response2 = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        SoapAssertionUtil<RegisterResponse> response3 = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue());

        Assertions.assertTrue(response.getResponse().getValue().getReturn().equals(response1.getResponse().getValue().getReturn()));
        Assertions.assertTrue(response.getResponse().getValue().getReturn().equals(response2.getResponse().getValue().getReturn()));
        Assertions.assertTrue(response.getResponse().getValue().getReturn().equals(response3.getResponse().getValue().getReturn()));
    }

    void executeHandleLookup(ContextRegistryClient client) throws Exception {

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

    void executeTestTimeToLive(ContextRegistryClient client, CacheProperties properties) throws Exception {
        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());

        // we need to sleep duration for 10 seconds of inactivity. Token should disappear
        Thread.sleep(CacheUtil.getMillisForTimeToIdle(properties));
        response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNull(response.getResponse().getValue().getReturn());
    }

    void executeTestTimeToIdle(ContextRegistryClient client, CacheProperties properties) throws Exception {
        String token = registerToken(client);

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse().getValue().getReturn());

        int backAThird = (int)properties.getEntryTimeToIdleSeconds() / 3;
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

    private String registerToken(ContextRegistryClient client) throws Exception {
        return registerToken(client, "user0");
    }

    private String registerToken(ContextRegistryClient client, String username) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/soapRequest.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", username);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/register/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<RegisterResponse> response = client.register(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        return response.getResponse().getValue().getReturn();
    }

    void executeBasicConcurrency(ContextRegistryClient client, int usersCount, CacheProperties properties) throws Exception {
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

    void executeTestHandleUnregister(ContextRegistryClient client) throws Exception {
        String token = registerToken(client);
        lookup(client, token);

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/unregister/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/unregister/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<UnregisterResponse> response = client.unregister(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse());

        soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        Assertions.assertNull(client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr).getResponse().getValue().getReturn());
    }

    void executeTestHandleUnregisterSharing(ContextRegistryClient client) throws Exception {
        String token = registerToken(client);
        lookup(client, token);

        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/unregister/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/unregister/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<UnregisterResponse> response = client.unregister(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        Assertions.assertNotNull(response.getResponse());

        soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        Assertions.assertNotNull(client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr).getResponse().getValue().getReturn());
    }

    private LookupResponse lookup(ContextRegistryClient client, String token) throws Exception {
        String soapRequestStr = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/soapRequest.xml");
        soapRequestStr = soapRequestStr.replace("${TOKEN}", token);

        String header = TestUtils.getContentsFromFile(
                "payloads/ctxtRegistry/lookup/requestHeaders.xml");

        client.setHeaderBlock(header);
        SoapAssertionUtil<LookupResponse> response = client.lookup(new URL(getGatewayUri() + "ContextRegistryService?wsdl"), soapRequestStr);
        return response.getResponse().getValue();
    }

    @Getter
    class RegisterThread implements Runnable {

        private ContextRegistryClient client;

        private String token;

        private String user;

        private CountDownLatch latch;

        private CacheProperties properties;

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
                throw new AssertionFailedError("The register operation errorred");
            }
        }
    }

    @Getter
    class LookupThread implements Runnable {

        private ContextRegistryClient client;

        private LookupResponse value;

        private String token;

        private CountDownLatch latch;

        private CacheProperties properties;

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
                throw new AssertionFailedError("The register operation errorred");
            }
        }
    }
}

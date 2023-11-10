package uk.gov.hmcts.darts.ws;

import contextreg.LookupResponse;
import contextreg.ObjectFactory;
import contextreg.RegisterResponse;
import contextreg.UnregisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class ContextRegistryEndpoint {

    public static final AtomicLong COUNTER = new AtomicLong();

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "register")
    @ResponsePayload
    public JAXBElement<RegisterResponse> register(@RequestPayload JAXBElement<contextreg.Register> addDocument) {
        RegisterResponse registerResponse = new RegisterResponse();

        // create a session as the client needs this
        createSession();

        // for now return a documentum id
        registerResponse.setReturn(getToken());
        return new ObjectFactory().createRegisterResponse(registerResponse);
    }

    private void createSession() {
        HttpServletRequest curRequest =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        curRequest.getSession();
    }

    private String getToken() {
        String machineIdentifier;
        try {
            machineIdentifier = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException var10) {
            machineIdentifier = "unknown";
        }
        int seedByteCount = 20;
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        byte[] seed = secureRandom.generateSeed(seedByteCount);
        secureRandom.setSeed(seed);
        String random = String.valueOf(secureRandom.nextLong());
        return machineIdentifier + "-" + System.currentTimeMillis() + "-" + random + "-" + COUNTER.incrementAndGet();
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "unregister")
    @ResponsePayload
    public JAXBElement<UnregisterResponse> unregister(@RequestPayload JAXBElement<contextreg.Unregister> addDocument) {
        UnregisterResponse registerResponse = new UnregisterResponse();
        return new ObjectFactory().createUnregisterResponse(registerResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "lookup")
    @ResponsePayload
    public JAXBElement<LookupResponse> lookup(@RequestPayload JAXBElement<contextreg.Lookup> addDocument) {
        LookupResponse registerResponse = new LookupResponse();
        return new ObjectFactory().createLookupResponse(registerResponse);
    }
}

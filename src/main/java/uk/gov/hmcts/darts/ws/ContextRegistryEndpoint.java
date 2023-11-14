package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.ctxtregistry.RefreshableCacheValue;
import uk.gov.hmcts.darts.ctxtregistry.TokenHolder;
import uk.gov.hmcts.darts.ctxtregistry.TokenRegisterable;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class ContextRegistryEndpoint {

    private final TokenRegisterable registerable;

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "register")
    @ResponsePayload
    public JAXBElement<RegisterResponse> register(@RequestPayload JAXBElement<documentum.contextreg.Register> addDocument) {
        RegisterResponse registerResponse = new RegisterResponse();

        // create a session as the client needs this
        TokenHolder holder = registerable.createToken(addDocument.getValue().getContext());
        registerable.store(holder, registerable.createValue(addDocument.getValue().getContext()));

        // for now return a documentum id
        registerResponse.setReturn(holder.getToken());
        return new ObjectFactory().createRegisterResponse(registerResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "unregister")
    @ResponsePayload
    public JAXBElement<UnregisterResponse> unregister(@RequestPayload JAXBElement<documentum.contextreg.Unregister> unregister) {
        UnregisterResponse unregisterResponse = new UnregisterResponse();

        registerable.evict(TokenHolder.generateToken(unregister.getValue().getToken()));

        return new ObjectFactory().createUnregisterResponse(unregisterResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "lookup")
    @ResponsePayload
    public JAXBElement<LookupResponse> lookup(@RequestPayload JAXBElement<documentum.contextreg.Lookup> lookup) {
        LookupResponse lookupResponse = new LookupResponse();
        RefreshableCacheValue value = registerable.lookup(TokenHolder.generateToken(lookup.getValue().getToken()));

        if (value != null) {
            lookupResponse.setReturn(value.getContext());
        }

        return new ObjectFactory().createLookupResponse(lookupResponse);
    }
}

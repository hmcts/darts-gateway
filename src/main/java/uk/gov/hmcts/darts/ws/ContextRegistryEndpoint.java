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
import uk.gov.hmcts.darts.cache.token.RefreshableCacheValue;
import uk.gov.hmcts.darts.cache.token.Token;
import uk.gov.hmcts.darts.cache.token.TokenRegisterable;

import java.util.Optional;

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
        Optional<Token> token = registerable.createToken(addDocument.getValue().getContext());

        if (token.isPresent()) {
            registerable.store(token.get(), registerable.createValue(addDocument.getValue().getContext()));

            // for now return a documentum id
            registerResponse.setReturn(token.get().getToken());
        }

        return new ObjectFactory().createRegisterResponse(registerResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "unregister")
    @ResponsePayload
    public JAXBElement<UnregisterResponse> unregister(@RequestPayload JAXBElement<documentum.contextreg.Unregister> unregister) {
        UnregisterResponse unregisterResponse = new UnregisterResponse();

        Optional<Token> token = registerable.getToken(unregister.getValue().getToken());

        token.ifPresent(registerable::evict);

        // TODO: Do we throw an exception here if token not found?
        return new ObjectFactory().createUnregisterResponse(unregisterResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "lookup")
    @ResponsePayload
    public JAXBElement<LookupResponse> lookup(@RequestPayload JAXBElement<documentum.contextreg.Lookup> lookup) {
        LookupResponse lookupResponse = new LookupResponse();
        Optional<Token> token = registerable.getToken(lookup.getValue().getToken());

        if (token.isPresent()) {
            RefreshableCacheValue value = registerable.lookup(token.get(), true);

            if (value != null) {
                lookupResponse.setReturn(value.getContext());
            }
        }

        // TODO: Do we throw an exception here if token not found?
        return new ObjectFactory().createLookupResponse(lookupResponse);
    }
}

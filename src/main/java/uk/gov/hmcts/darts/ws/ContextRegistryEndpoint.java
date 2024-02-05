package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.cache.token.exception.CacheTokenCreationException;
import uk.gov.hmcts.darts.cache.token.service.Token;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;
import uk.gov.hmcts.darts.cache.token.service.value.CacheValue;

import java.util.Optional;

@Endpoint
@Slf4j
public class ContextRegistryEndpoint {

    private final TokenRegisterable registerable;

    public ContextRegistryEndpoint(@Qualifier("primarycache") TokenRegisterable registerable) {
        this.registerable = registerable;
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "register")
    @ResponsePayload
    public JAXBElement<RegisterResponse> register(@RequestPayload JAXBElement<documentum.contextreg.Register> addDocument) {
        RegisterResponse registerResponse = new RegisterResponse();

        try {

            // create a session as the client needs this
            Optional<Token> cacheValue = registerable.store(addDocument.getValue().getContext());

            // for now return a documentum id
            cacheValue.ifPresent(value -> registerResponse.setReturn(value.getTokenString().orElse("")));
        } catch (CacheTokenCreationException cte) {
            log.warn("Failed creation of token", cte);
        }
        return new ObjectFactory().createRegisterResponse(registerResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "unregister")
    @ResponsePayload
    public JAXBElement<UnregisterResponse> unregister(@RequestPayload JAXBElement<documentum.contextreg.Unregister> unregister) {
        UnregisterResponse unregisterResponse = new UnregisterResponse();

        Token token = registerable.getToken(unregister.getValue().getToken());

        registerable.evict(token);

        return new ObjectFactory().createUnregisterResponse(unregisterResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "lookup")
    @ResponsePayload
    public JAXBElement<LookupResponse> lookup(@RequestPayload JAXBElement<documentum.contextreg.Lookup> lookup) {
        LookupResponse lookupResponse = new LookupResponse();
        Token token = registerable.getToken(lookup.getValue().getToken());

        Optional<CacheValue> value = registerable.lookup(token);
        value.ifPresent(refreshableCacheValue -> lookupResponse.setReturn(refreshableCacheValue.getServiceContext()));

        return new ObjectFactory().createLookupResponse(lookupResponse);
    }
}

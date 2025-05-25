package uk.gov.hmcts.darts.ws;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBElement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import uk.gov.hmcts.darts.authentication.exception.AuthenticationFailedException;
import uk.gov.hmcts.darts.authentication.exception.RegisterNullServiceContextException;
import uk.gov.hmcts.darts.cache.AuthSupport;

@Endpoint
@Slf4j
@AllArgsConstructor
public class ContextRegistryEndpoint {

    private final AuthSupport authSupport;


    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "register")
    @ResponsePayload
    public JAXBElement<RegisterResponse> register(@RequestPayload JAXBElement<Register> register) {

        validateRegisterBodyServiceContextIsNotNull(register);

        RegisterResponse registerResponse = new RegisterResponse();

        try {
            String token = authSupport.getOrCreateValidToken(register.getValue().getContext());
            authSupport.storeTokenContext(token, register.getValue().getContext());
            registerResponse.setReturn(token);
        } catch (AuthenticationFailedException cte) {
            log.warn("Failed creation of token", cte);
        }
        return new ObjectFactory().createRegisterResponse(registerResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "unregister")
    @ResponsePayload
    public JAXBElement<UnregisterResponse> unregister(@RequestPayload JAXBElement<documentum.contextreg.Unregister> unregister) {
        UnregisterResponse unregisterResponse = new UnregisterResponse();
        //No need to evict the token as it will be used by other services
        return new ObjectFactory().createUnregisterResponse(unregisterResponse);
    }

    @PayloadRoot(namespace = "http://services.rt.fs.documentum.emc.com/", localPart = "lookup")
    @ResponsePayload
    public JAXBElement<LookupResponse> lookup(@RequestPayload JAXBElement<documentum.contextreg.Lookup> lookup) {
        LookupResponse lookupResponse = new LookupResponse();
        lookupResponse.setReturn(authSupport.getServiceContextFromToken(lookup.getValue().getToken()));
        return new ObjectFactory().createLookupResponse(lookupResponse);
    }

    private static void validateRegisterBodyServiceContextIsNotNull(JAXBElement<Register> register) {
        if (register.getValue().getContext() == null) {
            throw new RegisterNullServiceContextException();
        }
    }
}

package uk.gov.hmcts.darts.utils.client.ctxt;

import documentum.contextreg.*;
import jakarta.xml.bind.JAXBElement;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.utils.client.SOAPAssertionUtil;
import uk.gov.hmcts.darts.utils.client.AbstractSOAPTestClient;

import java.net.URL;
import java.lang.Exception;

/**
 * Simple client that demonstrates Mtom interaction for the context registry api.
 */
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "unchecked"})
public class ContextRegistryMtomClient extends AbstractSOAPTestClient implements ContextRegistryClient {

    public ContextRegistryMtomClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public SOAPAssertionUtil<RegisterResponse> register(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           register -> new ObjectFactory().createRegister(register),
                           Register.class,
                           getCases -> (JAXBElement<RegisterResponse>) getCases
        );
    }

    @Override
    public SOAPAssertionUtil<LookupResponse> lookup(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           lookup -> new ObjectFactory().createLookup(lookup),
                           Lookup.class,
                           addDocument -> (JAXBElement<LookupResponse>) addDocument
        );
    }

    @Override
    public SOAPAssertionUtil<UnregisterResponse> unregister(URL uri, String payload) throws Exception {
        return sendMessage(uri, payload,
                           unregister -> new ObjectFactory().createUnregister(unregister),
                           Unregister.class,
                           unregister -> (JAXBElement<UnregisterResponse>) unregister
        );
    }

}

package uk.gov.hmcts.darts.common.utils.client.ctxt;

import documentum.contextreg.Lookup;
import documentum.contextreg.LookupResponse;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.Unregister;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.common.utils.client.AbstractSoapTestClient;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;

import java.net.URL;

/**
 * Simple client that demonstrates Mtom interaction for the context registry api.
 */
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "unchecked"})
public class ContextRegistryMtomClient extends AbstractSoapTestClient implements ContextRegistryClient {

    public ContextRegistryMtomClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    public SoapAssertionUtil<RegisterResponse> register(URL uri, String payload) throws JAXBException {
        return sendMessage(uri, payload,
                           register -> new ObjectFactory().createRegister(register),
                           Register.class,
                           getCases -> (JAXBElement<RegisterResponse>) getCases
        );
    }

    @Override
    public SoapAssertionUtil<LookupResponse> lookup(URL uri, String payload) throws JAXBException {
        return sendMessage(uri, payload,
                           lookup -> new ObjectFactory().createLookup(lookup),
                           Lookup.class,
                           addDocument -> (JAXBElement<LookupResponse>) addDocument
        );
    }

    @Override
    public SoapAssertionUtil<UnregisterResponse> unregister(URL uri, String payload) throws JAXBException {
        return sendMessage(uri, payload,
                           unregister -> new ObjectFactory().createUnregister(unregister),
                           Unregister.class,
                           unregister -> (JAXBElement<UnregisterResponse>) unregister
        );
    }

}

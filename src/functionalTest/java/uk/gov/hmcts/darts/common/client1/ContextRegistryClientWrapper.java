package uk.gov.hmcts.darts.common.client1;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBElement;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;
import uk.gov.hmcts.darts.contextregistry.response.CustomRegisterResponse;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

public class ContextRegistryClientWrapper extends AbstractSoapClient {

    private uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient client;

    public ContextRegistryClientWrapper(URL urlToCommunicateWith, ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping, uk.gov.hmcts.darts.utils.client.ctxt.ContextRegistryClient client) throws MalformedURLException {
        super(urlToCommunicateWith, externalUserToInternalUserMapping);
    }

    public uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse>  register(@RequestPayload JAXBElement<documentum.contextreg.Register> register) {
        client.setHeaderBlock("");

        return client.register();
    }

    public uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<LookupResponse> lookup(@RequestPayload JAXBElement<documentum.contextreg.Lookup> lookup) {
        return sendRequest(lookup, LookupResponse.class);
    }

    public uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<UnregisterResponse> unregister(@RequestPayload JAXBElement<documentum.contextreg.Unregister> unregister) {
        return sendRequest(unregister, UnregisterResponse.class);
    }
}

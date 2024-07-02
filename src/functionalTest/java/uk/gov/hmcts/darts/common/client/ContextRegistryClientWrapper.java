package uk.gov.hmcts.darts.common.client;

import documentum.contextreg.Lookup;
import documentum.contextreg.LookupResponse;
import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.Unregister;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBException;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

public class ContextRegistryClientWrapper extends AbstractSoapClient {

    private uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient client;

    public ContextRegistryClientWrapper(URI urlToCommunicateWith,
                                        ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping,
                                        ContextRegistryClient client) throws MalformedURLException {
        super(urlToCommunicateWith, externalUserToInternalUserMapping);
        this.client = client;
    }

    public uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<RegisterResponse>  register(
        @RequestPayload documentum.contextreg.Register register) throws JAXBException, IOException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "requestHeaders.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", externalUserToInternalUserMapping.getUserName());
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", externalUserToInternalUserMapping.getExternalPassword());

        client.setHeaderBlock(soapRequestStr);

        String registerStr = getStringFromClass(register);
        registerStr = registerStr.replace("${USER}", externalUserToInternalUserMapping.getUserName());
        registerStr = registerStr.replace("${PASSWORD}", externalUserToInternalUserMapping.getExternalPassword());

        return client.register(urlToCommunicateWith.toURL(), registerStr);
    }

    public uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<LookupResponse> lookup(
        @RequestPayload documentum.contextreg.Lookup lookup) throws JAXBException, IOException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "requestHeaders.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", externalUserToInternalUserMapping.getUserName());
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", externalUserToInternalUserMapping.getExternalPassword());

        client.setHeaderBlock(soapRequestStr);

        return client.lookup(urlToCommunicateWith.toURL(), getStringFromClass(lookup));
    }

    public uk.gov.hmcts.darts.utils.client.SoapAssertionUtil<UnregisterResponse> unregister(
        @RequestPayload documentum.contextreg.Unregister unregister) throws JAXBException, IOException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "requestHeaders.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", externalUserToInternalUserMapping.getUserName());
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", externalUserToInternalUserMapping.getExternalPassword());

        client.setHeaderBlock(soapRequestStr);

        return client.unregister(urlToCommunicateWith.toURL(), getStringFromClass(unregister));
    }

    public static Register getRegisterPayload() throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "soapRequestRegister.xml");

        return getClassFromString(soapRequestStr, Register.class);
    }

    public static Lookup getLookupPayload() throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "soapRequestLookup.xml");

        return getClassFromString(soapRequestStr, Lookup.class);
    }

    public static Unregister getUnregisterPayload() throws IOException, JAXBException {
        String soapRequestStr = TestUtils.getContentsFromFile(
            "soapRequestUnregister.xml");

        return getClassFromString(soapRequestStr, Unregister.class);
    }
}

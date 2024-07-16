package uk.gov.hmcts.darts.common.client;

import documentum.contextreg.Lookup;
import documentum.contextreg.LookupResponse;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.Register;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.Unregister;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.FileUtils;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;
import uk.gov.hmcts.darts.common.payload.SubstituteKey;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

public class ContextRegistryClientWrapper extends AbstractSoapClient {

    private final ContextRegistryClient client;

    public ContextRegistryClientWrapper(URI urlToCommunicateWith,
                                        ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping,
                                        ContextRegistryClient client) throws MalformedURLException {
        super(urlToCommunicateWith, externalUserToInternalUserMapping);
        this.client = client;
    }

    public SoapAssertionUtil<RegisterResponse> register(
        @RequestPayload Register register) throws JAXBException, IOException {
        client.setHeaderBlock(getHeader());

        String registerStr = getStringFromClass(new ObjectFactory().createRegister(register));
        registerStr = registerStr.replace(SubstituteKey.USER_NAME.getKey(), externalUserToInternalUserMapping.getUserName());
        registerStr = registerStr.replace(SubstituteKey.PASSWORD.getKey(), externalUserToInternalUserMapping.getExternalPassword());

        registerStr = registerStr.replace(SubstituteKey.USER_NAME.getKey(), externalUserToInternalUserMapping.getUserName());
        registerStr = registerStr.replace(SubstituteKey.PASSWORD.getKey(), externalUserToInternalUserMapping.getExternalPassword());

        if (hasToken()) {
            registerStr = registerStr.replace(SubstituteKey.TOKEN.getKey(), getToken());
        }

        return client.register(urlToCommunicateWith.toURL(), registerStr);
    }

    public SoapAssertionUtil<LookupResponse> lookup(
        @RequestPayload Lookup lookup) throws JAXBException, IOException {

        client.setHeaderBlock(getHeader());

        String lookupStr = getStringFromClass(new ObjectFactory().createLookup(lookup));

        lookupStr = lookupStr.replace(SubstituteKey.USER_NAME.getKey(), externalUserToInternalUserMapping.getUserName());
        lookupStr = lookupStr.replace(SubstituteKey.PASSWORD.getKey(), externalUserToInternalUserMapping.getExternalPassword());

        if (hasToken()) {
            lookupStr = lookupStr.replace(SubstituteKey.TOKEN.getKey(), getToken());
        }

        return client.lookup(urlToCommunicateWith.toURL(), lookupStr);
    }

    public SoapAssertionUtil<UnregisterResponse> unregister(
        @RequestPayload Unregister unregister) throws JAXBException, IOException {

        client.setHeaderBlock(getHeader());

        String unregisterStr = getStringFromClass(new ObjectFactory().createUnregister(unregister));

        // apply the token
        unregisterStr = unregisterStr.replace(SubstituteKey.USER_NAME.getKey(), externalUserToInternalUserMapping.getUserName());
        unregisterStr = unregisterStr.replace(SubstituteKey.PASSWORD.getKey(), externalUserToInternalUserMapping.getExternalPassword());

        if (hasToken()) {
            unregisterStr = unregisterStr.replace(SubstituteKey.TOKEN.getKey(), getToken());
        }

        return client.unregister(urlToCommunicateWith.toURL(), unregisterStr);
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

    private String getHeaderFile(String filelocation, String username, String password) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());

        String fileContents = FileUtils.readFileToString(file, "UTF-8");

        fileContents = fileContents.replace(SubstituteKey.USER_NAME.getKey(), username);
        fileContents = fileContents.replace(SubstituteKey.PASSWORD.getKey(), password);

        return fileContents;
    }

    private String getHeader() throws IOException {
        if (hasToken()) {
            return getHeaderFileToken("requestTokenHeader.xml", getToken());
        } else {
            return getHeaderFile(
                "requestHeaders.xml", externalUserToInternalUserMapping.getUserName(), externalUserToInternalUserMapping.getExternalPassword());
        }
    }

    private String getHeaderFileToken(String filelocation, String token) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());

        String fileContents = FileUtils.readFileToString(file, "UTF-8");

        fileContents = fileContents.replace(SubstituteKey.TOKEN.getKey(), token);

        return fileContents;
    }
}

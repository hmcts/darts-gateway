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
        registerStr = registerStr.replace("${USER}", externalUserToInternalUserMapping.getUserName());
        registerStr = registerStr.replace("${PASSWORD}", externalUserToInternalUserMapping.getExternalPassword());

        return client.register(urlToCommunicateWith.toURL(), registerStr);
    }

    public SoapAssertionUtil<LookupResponse> lookup(
        @RequestPayload Lookup lookup) throws JAXBException, IOException {

        client.setHeaderBlock(getHeader());

        String lookupStr = getStringFromClass(new ObjectFactory().createLookup(lookup));

        return client.lookup(urlToCommunicateWith.toURL(), lookupStr);
    }

    public SoapAssertionUtil<UnregisterResponse> unregister(
        @RequestPayload Unregister unregister) throws JAXBException, IOException {

        client.setHeaderBlock(getHeader());

        String unregisterStr = getStringFromClass(new ObjectFactory().createUnregister(unregister));

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

        fileContents = fileContents.replace("${USER}", username);
        fileContents = fileContents.replace("${PASSWORD}", password);

        return fileContents;
    }

    private String getHeader() throws IOException {
        if (!hasToken()) {
            return getHeaderFile(
                "requestHeaders.xml", externalUserToInternalUserMapping.getUserName(), externalUserToInternalUserMapping.getExternalPassword());
        } else {
            return getHeaderFileToken("requestTokenHeader.xml", getToken());
        }
    }

    private String getHeaderFileToken(String filelocation, String token) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(filelocation).getFile());

        String fileContents = FileUtils.readFileToString(file, "UTF-8");

        fileContents = fileContents.replace("${TOKEN}", token);

        return fileContents;
    }
}

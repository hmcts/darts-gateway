package uk.gov.hmcts.darts.testutils.request;

import documentum.contextreg.RegisterResponse;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;
import uk.gov.hmcts.darts.common.utils.TestUtils;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.testutils.stub.TokenStub;

import java.io.IOException;
import java.net.URL;

@Component
public class ContextRequestHelper {

    @Autowired
    private SecurityProperties securityProperties;

    protected TokenStub tokenStub = new TokenStub();

    /**
     * A helper method to allow a token to be successfully acquired. NOTE: Successful output depends on the profile
     * 'test-token' being active, or the relevant JWT validation infrastructure being mocked
     * @param client The client to use
     * @param gatewayUrl The gateway url to make a call
     * @return The response containing the token if successful
     */
    public SoapAssertionUtil<RegisterResponse> registerToken(ContextRegistryClient client, URL gatewayUrl) throws IOException, JAXBException {
        tokenStub.stubToken();
        tokenStub.stubJwksKeys();

        String soapHeaderServiceContextStr = TestUtils.getContentsFromFile(
            "payloads/soapHeaderServiceContext.xml");

        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace(
            "${USER}",
            securityProperties.getUserExternalInternalMappings().get(0).getUserName()
        );
        soapHeaderServiceContextStr = soapHeaderServiceContextStr.replace("${PASSWORD}",
                                                                          securityProperties.getUserExternalInternalMappings().get(0).getExternalPassword());

        // setup the header
        client.setHeaderBlock(soapHeaderServiceContextStr);

        // setup the register request
        String soapRequestStr = TestUtils.getContentsFromFile(
            "payloads/ctxtRegistry/register/soapRequest.xml");

        soapRequestStr = soapRequestStr.replace("${USER}", securityProperties.getUserExternalInternalMappings().get(0).getUserName());
        soapRequestStr = soapRequestStr.replace("${PASSWORD}", securityProperties.getUserExternalInternalMappings().get(0).getExternalPassword());

        return client.register(new URL(
            gatewayUrl + "ContextRegistryService?wsdl"), soapRequestStr);
    }
}

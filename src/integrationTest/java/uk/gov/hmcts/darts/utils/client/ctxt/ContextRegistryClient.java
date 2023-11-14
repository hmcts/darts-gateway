package uk.gov.hmcts.darts.utils.client.ctxt;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import uk.gov.hmcts.darts.utils.client.SoapAssertionUtil;
import uk.gov.hmcts.darts.utils.client.SoapTestClient;

import java.net.URL;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface ContextRegistryClient extends SoapTestClient {

    SoapAssertionUtil<RegisterResponse> register(URL uri, String payload) throws Exception;

    SoapAssertionUtil<LookupResponse> lookup(URL uri, String payload) throws Exception;

    SoapAssertionUtil<UnregisterResponse> unregister(URL uri, String payload) throws Exception;
}

package uk.gov.hmcts.darts.common.utils.client.ctxt;

import documentum.contextreg.LookupResponse;
import documentum.contextreg.RegisterResponse;
import documentum.contextreg.UnregisterResponse;
import jakarta.xml.bind.JAXBException;
import uk.gov.hmcts.darts.common.utils.client.SoapAssertionUtil;

import java.net.URL;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface ContextRegistryClient extends uk.gov.hmcts.darts.common.utils.client.SoapTestClient {

    SoapAssertionUtil<RegisterResponse> register(URL uri, String payload) throws JAXBException;

    SoapAssertionUtil<LookupResponse> lookup(URL uri, String payload) throws JAXBException;

    SoapAssertionUtil<UnregisterResponse> unregister(URL uri, String payload) throws JAXBException;
}

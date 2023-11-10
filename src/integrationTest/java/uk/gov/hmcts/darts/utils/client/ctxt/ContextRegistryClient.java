package uk.gov.hmcts.darts.utils.client.ctxt;

import contextreg.*;
import jakarta.xml.bind.JAXBElement;
import uk.gov.hmcts.darts.utils.client.SOAPAssertionUtil;

import java.net.URL;
import java.util.function.Function;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public interface ContextRegistryClient {

    SOAPAssertionUtil<RegisterResponse> register(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<LookupResponse> lookup(URL uri, String payload) throws Exception;

    SOAPAssertionUtil<UnregisterResponse> unregister(URL uri, String payload) throws Exception;
}

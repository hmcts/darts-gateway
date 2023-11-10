package uk.gov.hmcts.darts.utils.client.ctxt;

import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.annotation.PostConstruct;

/**
 * Simple client that demonstrates standard web services context registry = flow using text/xml format.
 */
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class ContextRegistryXmlClient extends ContextRegistryMtomClient {

    @PostConstruct
    public void init() throws Exception {
        setEnableMtomMode(false);
    }

    public ContextRegistryXmlClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }
}

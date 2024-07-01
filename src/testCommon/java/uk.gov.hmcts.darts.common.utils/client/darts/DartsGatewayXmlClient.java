package uk.gov.hmcts.darts.utils.client.darts;

import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.annotation.PostConstruct;

/**
 * Simple client that demonstrates standard web services flow using text/xml format.
 */
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class DartsGatewayXmlClient extends DartsGatewayMtomClient {

    @PostConstruct
    public void init() throws Exception {
        setEnableMtomMode(false);
    }

    public DartsGatewayXmlClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }
}

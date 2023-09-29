package uk.gov.hmcts.darts.utils.client;

import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.annotation.PostConstruct;
import java.lang.Exception;

/**
 * Simple client that demonstrates standard web services flow using non MTOM format
 */
public class DartsGatewayClient extends DartsGatewayMTOMClient {

    public DartsGatewayClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    @PostConstruct
    public void init() throws Exception {
        setEnableMTOMMode(false);
    }
}

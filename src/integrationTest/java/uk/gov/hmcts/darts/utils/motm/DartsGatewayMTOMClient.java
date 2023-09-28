package uk.gov.hmcts.darts.utils.motm;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Simple client that demonstrates MTOM by invoking {@code StoreImage} and {@code LoadImage} using a WebServiceTemplate
 * and SAAJ.
 *
 * @author Tareq Abed Rabbo
 * @author Arjen Poutsma
 */
public class MOTMClient extends WebServiceGatewaySupport {

    public MOTMClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }
}

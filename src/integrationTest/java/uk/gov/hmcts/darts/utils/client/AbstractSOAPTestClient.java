package uk.gov.hmcts.darts.utils.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.xml.transform.StringSource;

import java.net.URL;
import java.util.function.Function;

public abstract class AbstractSOAPTestClient extends WebServiceGatewaySupport {
    public AbstractSOAPTestClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
    }

    /**
     * sets the mode of this client to mtom supported or not.
     */
    public void setEnableMtomMode(boolean enabled) {
        Marshaller marshaller = getWebServiceTemplate().getMarshaller();
        Unmarshaller unmarshaller = this.getWebServiceTemplate().getUnmarshaller();

        if (marshaller instanceof Jaxb2Marshaller) {
            ((Jaxb2Marshaller) marshaller).setMtomEnabled(enabled);
        }

        if (unmarshaller instanceof Jaxb2Marshaller) {
            ((Jaxb2Marshaller) unmarshaller).setMtomEnabled(enabled);
        }
    }

    protected <I, O> SOAPAssertionUtil<O> sendMessage(URL uri, String payload,
                                                      Function<I,
                                                          JAXBElement<I>> supplier,
                                                      Class<I> clazz, Function<Object,
        JAXBElement<O>> responseSupplier)
        throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<I> unmarshal = jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
        JAXBElement<I> ijaxbElement = supplier.apply(unmarshal.getValue());
        return new SOAPAssertionUtil<>(responseSupplier.apply(getWebServiceTemplate().marshalSendAndReceive(
            uri.toString(),
            ijaxbElement
        )));
    }

    public <C> JAXBElement<C> convertData(String payload, Class<C> clazz)
        throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
    }


    protected void send(URL uri, String payload) throws Exception {
        getWebServiceTemplate().sendSourceAndReceiveToResult(
            uri.toString(),
            new StringSource(payload),
            new javax.xml.transform.Result() {
                @Override
                public void setSystemId(String systemId) {

                }

                @Override
                public String getSystemId() {
                    return null;
                }
            }
        );
    }
}

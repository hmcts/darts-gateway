package uk.gov.hmcts.darts.utils.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.xml.transform.StringSource;

import java.net.URL;
import java.util.function.Function;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

@SuppressWarnings("PMD.EmptyCatchBlock")
public abstract class AbstractSoapTestClient extends WebServiceGatewaySupport
    implements SoapTestClient {
    private String headerContents;

    private javax.xml.transform.Result getResult() {
        return new javax.xml.transform.Result() {
            @Override
            public void setSystemId(String systemId) {

            }

            @Override
            public String getSystemId() {
                return null;
            }
        };
    }

    public AbstractSoapTestClient(SaajSoapMessageFactory messageFactory) {
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

    protected <I, O> SoapAssertionUtil<O> sendMessage(URL uri, String payload,
                                                      Function<I,
                                                          JAXBElement<I>> supplier,
                                                      Class<I> clazz, Function<Object,
        JAXBElement<O>> responseSupplier)
        throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<I> unmarshal = jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
        JAXBElement<I> ijaxbElement = supplier.apply(unmarshal.getValue());

        // if we have no header don't add one otherwise add the header contents we have specified
        if (headerContents == null || headerContents.isEmpty()) {
            Object obj = getWebServiceTemplate().marshalSendAndReceive(
                uri.toString(),
                ijaxbElement
            );
            return new SoapAssertionUtil<>(responseSupplier.apply(obj));
        } else {

            Object obj = getWebServiceTemplate().marshalSendAndReceive(
                uri.toString(),
                ijaxbElement,
                new WebServiceMessageCallback() {

                    @Override
                    public void doWithMessage(WebServiceMessage message) {
                        try {
                            SoapMessage soapMessage = (SoapMessage) message;
                            SoapHeader header = soapMessage.getSoapHeader();
                            StringSource headerSource = new StringSource(headerContents);
                            Transformer transformer = TransformerFactory.newInstance().newTransformer();
                            transformer.transform(headerSource, header.getResult());
                        } catch (Exception e) {
                            // exception handling
                        }
                    }
                }
            );
            return new SoapAssertionUtil<>(responseSupplier.apply(obj));
        }
    }

    private WebServiceMessageCallback getSoapRequestHeader() {
        return new WebServiceMessageCallback() {

            @Override
            public void doWithMessage(WebServiceMessage message) {
                try {
                    SoapMessage soapMessage = (SoapMessage) message;
                    SoapHeader header = soapMessage.getSoapHeader();
                    StringSource headerSource = new StringSource(headerContents);
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.transform(headerSource, header.getResult());
                } catch (Exception e) {
                    // exception handling
                }
            }
        };
    }

    @Override
    public <C> JAXBElement<C> convertData(String payload, Class<C> clazz)
        throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
    }


    @Override
    public void send(URL uri, String payload) {
        WebServiceMessageCallback addHeader = getSoapRequestHeader();

        if (headerContents == null || headerContents.isEmpty()) {
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
        } else {
            getWebServiceTemplate().sendSourceAndReceiveToResult(
                uri.toString(),
                new StringSource(payload),
                addHeader,
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

    @Override
    public void setHeaderBlock(String header) {
        this.headerContents = header;
    }
}

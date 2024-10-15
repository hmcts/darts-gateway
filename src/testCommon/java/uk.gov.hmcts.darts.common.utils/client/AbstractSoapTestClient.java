package uk.gov.hmcts.darts.common.utils.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import org.springframework.xml.transform.StringSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.function.Function;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings("PMD.EmptyCatchBlock")
public abstract class AbstractSoapTestClient extends WebServiceGatewaySupport
    implements SoapTestClient {
    private String headerContents;

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

    public HttpUrlConnection sendMessageWithResponse(URL uri, String payload) {
        StreamSource stringSource = new StreamSource(IOUtils.toInputStream(payload, Charset.defaultCharset()), "");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        final ApplyHeaderWebServiceMessageCallback message = new ApplyHeaderWebServiceMessageCallback();

        // if we have no header don't add one otherwise add the header contents we have specified
        if (headerContents == null || headerContents.isEmpty()) {
            getWebServiceTemplate().sendSourceAndReceiveToResult(
                uri.toString(),
                stringSource,
                message,
                result
            );
        } else {
            getWebServiceTemplate().sendSourceAndReceiveToResult(
                uri.toString(),
                stringSource,
                message,
                result
            );
        }
        return message.getConnection();
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
            final ApplyHeaderWebServiceMessageCallback messageTransformer = new ApplyHeaderWebServiceMessageCallback();

            Object obj = getWebServiceTemplate().marshalSendAndReceive(
                uri.toString(),
                ijaxbElement,
                messageTransformer
            );
            return new SoapAssertionUtil<>(responseSupplier.apply(obj));
        }
    }

    private WebServiceMessageCallback getSoapRequestHeader() {
        final ApplyHeaderWebServiceMessageCallback messageTransformer = new ApplyHeaderWebServiceMessageCallback();
        return messageTransformer;
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

    class ApplyHeaderWebServiceMessageCallback implements WebServiceMessageCallback {
        private org.springframework.ws.transport.http.HttpUrlConnection connection;

        @Override
        public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
            TransportContext context = TransportContextHolder.getTransportContext();
            connection = (org.springframework.ws.transport.http.HttpUrlConnection) context.getConnection();

            SoapMessage soapMessage = (SoapMessage) message;
            SoapHeader header = soapMessage.getSoapHeader();
            StringSource headerSource = new StringSource(headerContents);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(headerSource, header.getResult());
        }

        /**
         * Expose the http connection so we can interrogate the http headers.
         * @return The connection
         */
        public org.springframework.ws.transport.http.HttpUrlConnection getConnection() {
            return connection;
        }
    }
}
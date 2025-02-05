package uk.gov.hmcts.darts.common.utils.client;

import com.emc.documentum.fs.rt.ServiceException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import lombok.SneakyThrows;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.xml.transform.StringSource;

import java.io.StringWriter;
import java.net.URL;
import java.util.function.Function;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("PMD.EmptyCatchBlock")
public abstract class AbstractSoapTestClient extends WebServiceGatewaySupport
    implements SoapTestClient {
    private final SoapMessageFactory soapMessageFactory;
    private String headerContents;

    public AbstractSoapTestClient(SaajSoapMessageFactory messageFactory) {
        super(messageFactory);
        this.soapMessageFactory = messageFactory;
    }

    public WebServiceTemplate getWebServiceTemplate(boolean addFaultHandler) {
        if (addFaultHandler) {
            WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.soapMessageFactory) {
                @SneakyThrows
                @Override
                protected Object handleFault(WebServiceConnection connection, MessageContext messageContext) {
                    SoapFault soapFault = ((SoapMessage) messageContext.getResponse()).getSoapBody().getFault();
                    assertThat(soapFault).isNotNull();

                    String responseXml = toXmlString(new DOMSource(((SoapMessage) messageContext.getResponse()).getDocument()));
                    String fault = responseXml
                        .replaceFirst("</ns3:ServiceException>(.|\\n)*$", "</ns3:ServiceException>");
                    fault = fault.substring(fault.indexOf("<ns3:ServiceException"));
                    JAXBContext jaxbContext = JAXBContext.newInstance(ServiceException.class);
                    jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    return jaxbUnmarshaller.unmarshal(new StringSource(fault), ServiceException.class);
                }
            };
            webServiceTemplate.setMarshaller(getMarshaller());
            webServiceTemplate.setUnmarshaller(getUnmarshaller());
            return webServiceTemplate;
        } else {
            return getWebServiceTemplate();
        }
    }

    @SneakyThrows
    public static String toXmlString(Source xmlSource) {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(xmlSource, new StreamResult(sw));
        return sw.toString();
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
                                                      Function<I, JAXBElement<I>> supplier,
                                                      Class<I> clazz, Function<Object,
            JAXBElement<O>> responseSupplier) {
        return sendMessage(uri, payload, supplier, clazz, responseSupplier, false);
    }

    @SneakyThrows
    protected <I, O> SoapAssertionUtil<O> sendMessage(URL uri, String payload,
                                                      Function<I, JAXBElement<I>> supplier,
                                                      Class<I> clazz, Function<Object,
            JAXBElement<O>> responseSupplier, boolean addFaultHandler) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<I> unmarshal = jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
        JAXBElement<I> ijaxbElement = supplier.apply(unmarshal.getValue());

        // if we have no header don't add one otherwise add the header contents we have specified
        if (headerContents == null || headerContents.isEmpty()) {
            Object obj = getWebServiceTemplate(addFaultHandler).marshalSendAndReceive(
                uri.toString(),
                ijaxbElement
            );
            return new SoapAssertionUtil<>(responseSupplier.apply(obj));
        } else {
            Object obj = getWebServiceTemplate(addFaultHandler)
                .marshalSendAndReceive(
                    uri.toString(),
                    ijaxbElement,
                    message -> {
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
                );
            return new SoapAssertionUtil<>(responseSupplier.apply(obj));

        }
    }

    private WebServiceMessageCallback getSoapRequestHeader() {
        return message -> {
            try {
                SoapMessage soapMessage = (SoapMessage) message;
                SoapHeader header = soapMessage.getSoapHeader();
                StringSource headerSource = new StringSource(headerContents);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(headerSource, header.getResult());
            } catch (Exception e) {
                // exception handling
            }
        };
    }

    @Override
    @SneakyThrows
    public <C> JAXBElement<C> convertData(String payload, Class<C> clazz) {
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

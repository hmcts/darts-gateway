package uk.gov.hmcts.darts.common.client;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class AbstractSoapClient {

    private static final Log LOG = LogFactory.getLog(AbstractSoapClient.class);

    protected ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping;

    protected final URI urlToCommunicateWith;

    public AbstractSoapClient(URI urlToCommunicateWith, ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping) throws MalformedURLException {
        this.urlToCommunicateWith = urlToCommunicateWith;
        this.externalUserToInternalUserMapping = externalUserToInternalUserMapping;
    }

    public static <T> T  getClassFromString(String payload, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        jakarta.xml.bind.Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<T> unmarshal = jaxbUnmarshaller.unmarshal(new StringSource(payload), clazz);
        return unmarshal.getValue();
    }

    public static String getStringFromClass(Object object) throws JAXBException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(object, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }
}

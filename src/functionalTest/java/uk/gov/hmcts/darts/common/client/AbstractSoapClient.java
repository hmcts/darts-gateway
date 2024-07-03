package uk.gov.hmcts.darts.common.client;

import documentum.contextreg.Lookup;
import documentum.contextreg.ObjectFactory;
import documentum.contextreg.Register;
import documentum.contextreg.Unregister;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.Getter;
import lombok.Setter;
import org.springframework.xml.transform.StringSource;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class AbstractSoapClient {
    @Setter
    @Getter
    protected ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping;

    protected final URI urlToCommunicateWith;

    @Setter
    @Getter
    private String token;

    /**
     * Clear down token to switch back to username password authentication.
     */
    public void clearToken() {
        token = "";
    }

    public boolean hasToken() {
        return token != null && !token.isEmpty();
    }

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

    public <T> String getStringFromClass(JAXBElement<T> object) throws JAXBException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass(), Register.class, Lookup.class, Unregister.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(object, byteArrayOutputStream);

        // apply the token
        String retString = byteArrayOutputStream.toString().replace("${USER}", externalUserToInternalUserMapping.getUserName());
        retString = retString.replace("${PASSWORD}", externalUserToInternalUserMapping.getExternalPassword());

        if (hasToken()) {
            retString = retString.replace("${TOKEN}", token);
        }

        return retString;
    }
}

package uk.gov.hmcts.darts.common.client1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.gov.hmcts.darts.cache.token.config.impl.ExternalUserToInternalUserMappingImpl;
import uk.gov.hmcts.darts.common.util.LogUtil;
import uk.gov.hmcts.darts.dartsapi.response.ServiceExceptionResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class AbstractSoapClient {

    private static final Log LOG = LogFactory.getLog(AbstractSoapClient.class);

    private ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping;

    private final URL urlToCommunicateWith;

    public AbstractSoapClient(URL urlToCommunicateWith, ExternalUserToInternalUserMappingImpl externalUserToInternalUserMapping) throws MalformedURLException {
        this.urlToCommunicateWith = urlToCommunicateWith;
        this.externalUserToInternalUserMapping = externalUserToInternalUserMapping;
    }
}

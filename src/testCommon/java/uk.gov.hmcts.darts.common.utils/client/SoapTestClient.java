package uk.gov.hmcts.darts.common.utils.client;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.springframework.ws.transport.http.HttpUrlConnection;

import java.net.URL;

public interface SoapTestClient {
    <C> JAXBElement<C> convertData(String payload, Class<C> clazz) throws JAXBException;

    void setHeaderBlock(String header);

    void send(URL uri, String payload);

    HttpUrlConnection sendMessageWithResponse(URL uri, String payload);
}
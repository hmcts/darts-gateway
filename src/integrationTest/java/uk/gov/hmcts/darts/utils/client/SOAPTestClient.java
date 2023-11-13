package uk.gov.hmcts.darts.utils.client;

import jakarta.xml.bind.JAXBElement;

import java.net.URL;

public interface SOAPTestClient {
    <C> JAXBElement<C> convertData(String payload, Class<C> clazz) throws Exception;

    void setHeaderBlock(String header);

    void send(URL uri, String payload) throws Exception;
}

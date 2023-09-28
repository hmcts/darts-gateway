package uk.gov.hmcts.darts.motm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.motm.MOTMClient;

@Configuration
public class MtomClientConfig {

    @Bean
    public MOTMClient saajClient(SaajSoapMessageFactory messageFactory, Jaxb2Marshaller marshaller) {

        MOTMClient client = new MOTMClient(messageFactory);
        client.setDefaultUri("http://localhost:8080/mtom-server/services");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPaths("com.emc.documentum.fs", "com.service.mojdarts.synapps.com", "com.synapps.moj.dfs.response");
        marshaller.setMtomEnabled(true);
        return marshaller;
    }

}

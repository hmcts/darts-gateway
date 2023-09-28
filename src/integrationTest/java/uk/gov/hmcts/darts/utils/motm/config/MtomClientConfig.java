package uk.gov.hmcts.darts.utils.motm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.utils.motm.DartsGatewayMTOMClient;

@Configuration
public class MtomClientConfig {

    @Bean
    public DartsGatewayMTOMClient saajClient(SaajSoapMessageFactory messageFactory) {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.emc.documentum.fs", "com.service.mojdarts.synapps.com", "com.synapps.moj.dfs.response");
        marshaller.setMtomEnabled(true);

        DartsGatewayMTOMClient client = new DartsGatewayMTOMClient(messageFactory);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory() {
        return new SaajSoapMessageFactory();
    }
}

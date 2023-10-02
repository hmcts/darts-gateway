package uk.gov.hmcts.darts.utils.motm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.utils.client.DartsGatewayMtomClient;
import uk.gov.hmcts.darts.utils.client.DartsGatewayXmlClient;

@Configuration
public class MtomClientConfig {

    @Bean
    public DartsGatewayMtomClient saajClient(SaajSoapMessageFactory messageFactory) {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.emc.documentum.fs", "com.service.mojdarts.synapps.com", "com.synapps.moj.dfs.response");
        marshaller.setMtomEnabled(true);

        DartsGatewayMtomClient client = new DartsGatewayMtomClient(messageFactory);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public DartsGatewayXmlClient vanillaClient(SaajSoapMessageFactory messageFactory) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.emc.documentum.fs", "com.service.mojdarts.synapps.com", "com.synapps.moj.dfs.response");
        marshaller.setMtomEnabled(true);

        DartsGatewayXmlClient client = new DartsGatewayXmlClient(messageFactory);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory() {
        return new SaajSoapMessageFactory();
    }
}

package uk.gov.hmcts.darts.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryMtomClient;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayMtomClient;
import uk.gov.hmcts.darts.common.utils.client.darts.DartsGatewayXmlClient;

@Configuration
public class MtomClientConfig {

    @Bean
    public DartsGatewayMtomClient mtomClient(SaajSoapMessageFactory messageFactory) {

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
        marshaller.setMtomEnabled(false);

        DartsGatewayXmlClient client = new DartsGatewayXmlClient(messageFactory);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public ContextRegistryClient mtomClientContextRegistry(SaajSoapMessageFactory messageFactory) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("documentum.contextreg");
        marshaller.setMtomEnabled(true);

        ContextRegistryMtomClient client = new ContextRegistryMtomClient(messageFactory);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public ContextRegistryMtomClient vanillaClientContextRegistry(SaajSoapMessageFactory messageFactory) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("documentum.contextreg");
        marshaller.setMtomEnabled(false);

        ContextRegistryMtomClient client = new ContextRegistryMtomClient(messageFactory);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);

        return client;
    }

    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory() {
        return new SaajSoapMessageFactory();
    }
}

package uk.gov.hmcts.darts.common.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryMtomClient;
import uk.gov.hmcts.darts.properties.FunctionalProperties;

import java.net.MalformedURLException;

@Configuration
public class ClientConfiguration {
    @Autowired
    private FunctionalProperties functionalProperties;

    @Autowired
    private ContextRegistryClient client;

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

    @Bean("viqClient")
    public ContextRegistryClientWrapper getViq() throws MalformedURLException {
        return new ContextRegistryClientWrapper(functionalProperties.getDeployedApplicationUri(), functionalProperties.getViq(), client);
    }

    @Bean("xhibitClient")
    public ContextRegistryClientWrapper getXhibit() throws MalformedURLException {
        return new ContextRegistryClientWrapper(functionalProperties.getDeployedApplicationUri(), functionalProperties.getXhibit(), client);
    }

    @Bean("cppClient")
    public ContextRegistryClientWrapper getCpp() throws MalformedURLException {
        return new ContextRegistryClientWrapper(functionalProperties.getDeployedApplicationUri(), functionalProperties.getCpp(), client);
    }
}

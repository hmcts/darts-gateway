package uk.gov.hmcts.darts.common.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.darts.common.client.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.common.client.FunctionalTestClient;
import uk.gov.hmcts.darts.common.utils.client.ctxt.ContextRegistryMtomClient;
import uk.gov.hmcts.darts.properties.FunctionalProperties;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@TestConfiguration
@EnableConfigurationProperties
public class ContextClientConfiguration {

    public static final String WEB_CONTEXT = "/service/darts";

    @Autowired
    private FunctionalProperties functionalProperties;

    @Autowired
    private ContextRegistryMtomClient client;

    @Bean("viqClient")
    public ContextRegistryClientWrapper getViq() throws MalformedURLException, URISyntaxException {
        return new ContextRegistryClientWrapper(new URI(functionalProperties.getDeployedApplicationUri()
                                                            + WEB_CONTEXT), functionalProperties.getViq(), client);
    }

    @Bean("xhibitClient")
    public ContextRegistryClientWrapper getXhibit() throws MalformedURLException, URISyntaxException {
        return new ContextRegistryClientWrapper(new URI(functionalProperties.getDeployedApplicationUri()
                                                            + WEB_CONTEXT), functionalProperties.getXhibit(), client);
    }

    @Bean("cppClient")
    public ContextRegistryClientWrapper getCpp() throws MalformedURLException, URISyntaxException {
        return new ContextRegistryClientWrapper(new URI(functionalProperties.getDeployedApplicationUri()
                                                            + WEB_CONTEXT), functionalProperties.getCpp(), client);
    }

    @Bean
    public FunctionalTestClient getFunctionalClient() {
        return new FunctionalTestClient(functionalProperties
                                            .getDeployedApplicationUri().toString());
    }
}
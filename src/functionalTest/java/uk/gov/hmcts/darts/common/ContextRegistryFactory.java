package uk.gov.hmcts.darts.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.darts.common.client1.ContextRegistryClientWrapper;
import uk.gov.hmcts.darts.properties.FunctionalProperties;

import java.net.URI;

@Component
public class ContextRegistryFactory {
    @Autowired
    private FunctionalProperties functionalProperties;

    public ContextRegistryClientWrapper getContextRegistryClientWithViqClient(URI baseUri) {
        return new ContextRegistryClientWrapper(baseUri, functionalProperties.getViq());
    }

    public ContextRegistryClientWrapper getContextRegistryClientWithCppClient(URI baseUri) {
        return new ContextRegistryClientWrapper(baseUri, functionalProperties.getCpp());
    }

    public ContextRegistryClientWrapper getContextRegistryClientWithXhibitClient(URI baseUri) {
        return new ContextRegistryClientWrapper(baseUri, functionalProperties.getXhibit());
    }
}

package uk.gov.hmcts.darts.metadata;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ContextRegistryEndpointMetaData implements EndpointMetaData {

    private final String webcontext;

    public ContextRegistryEndpointMetaData(String webcontext) {
        this.webcontext = webcontext;
    }

    @Override
    public Resource[] getResources() {
        List<Resource> schemaResources = new ArrayList<>();
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema1.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema2.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema3.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema4.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema5.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema6.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema7.xsd"));
        schemaResources.add(new ClassPathResource("ws/contextRegistry/ContextRegistryService_schema8.xsd"));
        return schemaResources.toArray(new Resource[0]);
    }

    @Override
    public Resource getConsolidatedWsdlStream() {
        return new ClassPathResource("ws/ContextRegistryService.wsdl");
    }

    @Override
    public Pattern getEndpointMatcher() {
        return Pattern.compile(webcontext.replace("/", "\\/") + "*\\/ContextRegistryService");
    }
}

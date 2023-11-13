package uk.gov.hmcts.darts.metadata;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DartsSOAPEndpointMetaData implements EndpointMetaData {

    private final String webcontext;

    public DartsSOAPEndpointMetaData(String webcontext) {
        this.webcontext = webcontext;
    }

    public static final String SCHEMAS_DARTS_ADD_CASE_XSD = "schemas/darts-add-case.xsd";

    public static final String SCHEMAS_DARTS_REGISTER_NODE_XSD = "schemas/darts-register-node.xsd";

    @Override
    public Resource[] getResources() {
        List<Resource> schemaResources = new ArrayList<>();
        schemaResources.add(new ClassPathResource(SCHEMAS_DARTS_ADD_CASE_XSD));
        schemaResources.add(new ClassPathResource(SCHEMAS_DARTS_REGISTER_NODE_XSD));
        schemaResources.add(new ClassPathResource("ws/dartsService/DARTSService_schema1.xsd"));
        schemaResources.add(new ClassPathResource("ws/dartsService/DARTSService_schema2.xsd"));
        schemaResources.add(new ClassPathResource("ws/dartsService/DARTSService_schema3.xsd"));
        schemaResources.add(new ClassPathResource("ws/dartsService/DARTSService_schema4.xsd"));
        schemaResources.add(new ClassPathResource("ws/dartsService/DARTSService_schema5.xsd"));
        schemaResources.add(new ClassPathResource("ws/dartsService/DARTSService_schema6.xsd"));
        return schemaResources.toArray(new Resource[0]);
    }

    @Override
    public Resource getConsolidatedWsdlStream() {
        return new ClassPathResource("ws/dartsService.wsdl");
    }

    @Override
    public Pattern getEndpointMatcher() {
        return Pattern.compile(webcontext.replace("/", "\\/") + "*\\/DARTSService");
    }
}

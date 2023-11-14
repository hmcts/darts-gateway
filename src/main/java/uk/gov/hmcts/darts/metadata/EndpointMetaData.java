package uk.gov.hmcts.darts.metadata;

import org.springframework.core.io.Resource;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface EndpointMetaData {
    Resource[] getResources();

    default XsdSchema[] getSchemas() {
        List<XsdSchema> schemas;
        schemas = Arrays.stream(getResources()).map(SimpleXsdSchema::new).collect(Collectors.toList());
        return schemas.toArray(new XsdSchema[0]);
    }

    Resource getConsolidatedWsdlStream();

    Pattern getEndpointMatcher();

    default Wsdl11Definition getWsdlDefinition() {
        var wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(getConsolidatedWsdlStream());
        return wsdl11Definition;
    }
}

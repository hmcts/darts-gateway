package uk.gov.hmcts.darts.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;

import java.util.ArrayList;
import java.util.List;

@EnableWs
@Configuration
public class SoapWebServiceConfig extends WsConfigurerAdapter {

    public static final String SCHEMAS_DARTS_WS_SCHEMA_6_XSD = "ws/dartsService/DARTSService_schema6.xsd";
    public static final String SCHEMAS_DARTS_ADD_CASE_XSD = "schemas/darts-add-case.xsd";

    public static final String SCHEMAS_DARTS_REGISTER_NODE_XSD = "schemas/darts-register-node.xsd";
    @Value("${darts-gateway.ws.request-validation}")
    private boolean requestValidation;

    @Value("${darts-gateway.ws.response-validation}")
    private boolean responseValidation;

    @Bean
    public XsdSchema rootSchema() {
        return new SimpleXsdSchema(new ClassPathResource(SCHEMAS_DARTS_WS_SCHEMA_6_XSD));
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        var validatingInterceptor = new PayloadValidatingInterceptor();
        validatingInterceptor.setValidateRequest(requestValidation);
        validatingInterceptor.setValidateResponse(responseValidation);
        validatingInterceptor.setXsdSchema(rootSchema());
        XsdSchemaCollection schemaCollection = new XsdSchemaCollection() {
            @Override
            public XsdSchema[] getXsdSchemas() {
                List<XsdSchema> schemas = new ArrayList<>();
                schemas.add(rootSchema());
                schemas.add(new SimpleXsdSchema(new ClassPathResource(SCHEMAS_DARTS_ADD_CASE_XSD)));
                schemas.add(new SimpleXsdSchema(new ClassPathResource(SCHEMAS_DARTS_REGISTER_NODE_XSD)));
                return schemas.toArray(new XsdSchema[0]);
            }

            @SneakyThrows
            @Override
            public XmlValidator createValidator() {
                return XmlValidatorFactory.createValidator(getSchemas(), "http://www.w3.org/2001/XMLSchema");

            }
        };
        validatingInterceptor.setXsdSchemaCollection(schemaCollection);
        interceptors.add(validatingInterceptor);
    }

    public Resource[] getSchemas() {
        List<Resource> schemaResources = new ArrayList<>();
        schemaResources.add(new ClassPathResource(SCHEMAS_DARTS_WS_SCHEMA_6_XSD));
        schemaResources.add(new ClassPathResource(SCHEMAS_DARTS_ADD_CASE_XSD));
        schemaResources.add(new ClassPathResource(SCHEMAS_DARTS_REGISTER_NODE_XSD));
        return schemaResources.toArray(new Resource[0]);
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        var messageDispatcherServlet = new MessageDispatcherServlet();
        messageDispatcherServlet.setApplicationContext(context);
        messageDispatcherServlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(messageDispatcherServlet, "/ws/*");
    }

    @Bean(name = "darts")
    public Wsdl11Definition dartsWsdl11Definition() {
        var wsdl11Definition = new SimpleWsdl11Definition();
        wsdl11Definition.setWsdl(new ClassPathResource("/ws/dartsService.wsdl"));
        return wsdl11Definition;
    }
}

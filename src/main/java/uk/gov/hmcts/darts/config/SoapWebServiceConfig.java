package uk.gov.hmcts.darts.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;
import uk.gov.hmcts.darts.authentication.component.SoapRequestInterceptor;
import uk.gov.hmcts.darts.metadata.ContextRegistryEndpointMetaData;
import uk.gov.hmcts.darts.metadata.DartsEndpointMetaData;
import uk.gov.hmcts.darts.metadata.EndpointMetaData;
import uk.gov.hmcts.darts.ws.DartsMessageDispatcherServlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableWs
@Configuration
@RequiredArgsConstructor
public class SoapWebServiceConfig extends WsConfigurerAdapter {

    private final SoapRequestInterceptor soapRequestInterceptor;

    @Value("${darts-gateway.ws.request-validation}")
    private boolean requestValidation;

    @Value("${darts-gateway.ws.response-validation}")
    private boolean responseValidation;

    private static final String BASE_WEB_CONTEXT = "/service/darts/";

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(soapRequestInterceptor);

        var validatingInterceptor = new PayloadValidatingInterceptor();
        validatingInterceptor.setValidateRequest(requestValidation);
        validatingInterceptor.setValidateResponse(responseValidation);
        XsdSchemaCollection schemaCollection = new XsdSchemaCollection() {
            @Override
            public XsdSchema[] getXsdSchemas() {
                List<XsdSchema> schemas = new ArrayList<>();

                for (EndpointMetaData metaData : getEndpointMetaData()) {
                    schemas.addAll(Arrays.asList(metaData.getSchemas()));
                }

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
        for (EndpointMetaData metaData : getEndpointMetaData()) {
            schemaResources.addAll(Arrays.asList(metaData.getResources()));
        }

        return schemaResources.toArray(new Resource[0]);
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context, List<EndpointMetaData> metaData) {
        var messageDispatcherServlet = new DartsMessageDispatcherServlet(metaData);
        messageDispatcherServlet.setApplicationContext(context);
        return new ServletRegistrationBean<>(messageDispatcherServlet, BASE_WEB_CONTEXT + "*");
    }

    @Bean
    public List<Wsdl11Definition> dartsWsdl11Definition(List<EndpointMetaData> metaData) {
        return metaData.stream().map(EndpointMetaData::getWsdlDefinition).collect(Collectors.toList());
    }

    @Bean
    List<EndpointMetaData> getEndpointMetaData() {
        List<EndpointMetaData> endpointMetaData = new ArrayList<>();
        endpointMetaData.add(new DartsEndpointMetaData(BASE_WEB_CONTEXT));
        endpointMetaData.add(new ContextRegistryEndpointMetaData(BASE_WEB_CONTEXT));
        return endpointMetaData;
    }

}

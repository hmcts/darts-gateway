package uk.gov.hmcts.darts.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import uk.gov.hmcts.darts.authentication.component.SoapRequestInterceptor;
import uk.gov.hmcts.darts.common.multipart.JavaMailXmlWithFileMultiPartRequestFactory;
import uk.gov.hmcts.darts.metadata.EndpointMetaData;
import uk.gov.hmcts.darts.ws.DartsMessageDispatcherServlet;

import java.util.List;
import java.util.stream.Collectors;

@EnableWs
@Configuration
@RequiredArgsConstructor
public class SoapWebServiceConfig extends WsConfigurerAdapter {



    public static final String BASE_WEB_CONTEXT = "/service/darts/";

    private final SoapRequestInterceptor soapRequestInterceptor;

    private final DartsPayloadValidatingInterceptor validatingInterceptor;

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(soapRequestInterceptor);
        interceptors.add(validatingInterceptor);
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
        ApplicationContext context, List<EndpointMetaData> metaData, JavaMailXmlWithFileMultiPartRequestFactory factory) {
        var messageDispatcherServlet = new DartsMessageDispatcherServlet(metaData, factory);
        messageDispatcherServlet.setApplicationContext(context);
        return new ServletRegistrationBean<>(messageDispatcherServlet, BASE_WEB_CONTEXT + "*");
    }

    @Bean
    public List<Wsdl11Definition> dartsWsdl11Definition(List<EndpointMetaData> metaData) {
        return metaData.stream().map(EndpointMetaData::getWsdlDefinition).collect(Collectors.toList());
    }
}

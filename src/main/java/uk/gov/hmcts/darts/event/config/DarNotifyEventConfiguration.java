package uk.gov.hmcts.darts.event.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@Configuration
@EnableConfigurationProperties(DarNotifyEventConfigurationProperties.class)
public class DarNotifyEventConfiguration {

    @Bean
    public Jaxb2Marshaller marshaller() {
        var marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.viqsoultions");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate darNotifyEventWebServiceTemplate(
        DarNotifyEventConfigurationProperties darNotifyEventConfigurationProperties,
        Jaxb2Marshaller marshaller,
        Wss4jSecurityInterceptor securityInterceptor) {
        var webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setDefaultUri(darNotifyEventConfigurationProperties.getDefaultNotificationUrl().toString());
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setInterceptors(new ClientInterceptor[]{securityInterceptor});
        return webServiceTemplate;
    }

    @Bean
    public Wss4jSecurityInterceptor securityInterceptor(
        DarNotifyEventConfigurationProperties darNotifyEventConfigurationProperties) {
        var securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setSecurementActions(darNotifyEventConfigurationProperties.getSecurementActions());
        securityInterceptor.setSecurementUsername(darNotifyEventConfigurationProperties.getSecurementUsername());
        securityInterceptor.setSecurementPassword(darNotifyEventConfigurationProperties.getSecurementPassword());
        return securityInterceptor;
    }

}

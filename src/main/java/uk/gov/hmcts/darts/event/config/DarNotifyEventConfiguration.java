package uk.gov.hmcts.darts.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@Configuration
public class DarNotifyEventConfiguration {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.viqsoultions");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate darNotifyEventWebServiceTemplate(Jaxb2Marshaller marshaller,
                                                               Wss4jSecurityInterceptor securityInterceptor) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setDefaultUri("http://localhost:8080/VIQDARNotifyEvent/DARNotifyEvent.asmx");
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setInterceptors(new ClientInterceptor[]{securityInterceptor});
        return webServiceTemplate;
    }

    @Bean
    public Wss4jSecurityInterceptor securityInterceptor() {
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setSecurementActions("UsernameToken");
        securityInterceptor.setSecurementUsername("username");
        securityInterceptor.setSecurementPassword("password");
        return securityInterceptor;
    }

}

package uk.gov.hmcts.darts.event.config;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import uk.gov.hmcts.darts.log.api.LogApi;
import uk.gov.hmcts.darts.log.api.impl.LogApiImpl;
import uk.gov.hmcts.darts.log.service.impl.DarNotificationLoggerServiceImpl;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(DarNotifyEventConfigurationProperties.class)
public class DarNotifyEventConfiguration {

    public HttpComponentsMessageSender httpComponentsMessageSender() {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setAcceptGzipEncoding(false);
        return httpComponentsMessageSender;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.viqsoultions");
        marshaller.setMarshallerProperties(Map.of("org.glassfish.jaxb.namespacePrefixMapper", new ViqSolutionsNamespaceMapper()));
        marshaller.setValidationEventHandler(new  jakarta.xml.bind.helpers.DefaultValidationEventHandler());
        return marshaller;
    }

    @Bean
    @Primary
    public WebServiceTemplate darNotifyEventWebServiceTemplate(Jaxb2Marshaller marshaller) {
        var webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        webServiceTemplate.setMessageSender(httpComponentsMessageSender());
        return webServiceTemplate;
    }

    @Bean
    public String soapAction(DarNotifyEventConfigurationProperties darNotifyEventConfigurationProperties) {
        return darNotifyEventConfigurationProperties.getSoapAction().toExternalForm();
    }

    @Bean
    public LogApi logApi() {
        return new LogApiImpl(
            new DarNotificationLoggerServiceImpl());
    }

    private static final class ViqSolutionsNamespaceMapper extends NamespacePrefixMapper {

        static final String URL = "http://www.VIQSoultions.com";

        @Override
        public String getPreferredPrefix(String uri, String suggest, boolean require) {
            if (URL.equals(uri)) {
                return "";
            }

            return suggest;
        }

        @Override
        public String[] getPreDeclaredNamespaceUris() {
            return new String[0];
        }
    }
}

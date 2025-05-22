package uk.gov.hmcts.darts.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.xml.sax.SAXException;
import uk.gov.hmcts.darts.metadata.EndpointMetaData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.xml.transform.TransformerException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DartsPayloadValidatingInterceptor extends PayloadValidatingInterceptor {

    @Value("${darts-gateway.ws.request-validation}")
    private boolean requestValidation;

    @Value("${darts-gateway.ws.response-validation}")
    private boolean responseValidation;

    @Value("${darts-gateway.addcase.validate}")
    private boolean validateAddCase;

    private final List<EndpointMetaData> endpointMetaDataList;

    @PostConstruct
    void postConstruct() {
        this.setValidateRequest(requestValidation);
        this.setValidateResponse(responseValidation);


        XsdSchemaCollection schemaCollection = new XsdSchemaCollection() {
            @Override
            public XsdSchema[] getXsdSchemas() {
                List<XsdSchema> schemas = new ArrayList<>();

                for (EndpointMetaData metaData : endpointMetaDataList) {
                    schemas.addAll(Arrays.asList(metaData.getSchemas()));
                }

                return schemas.toArray(new XsdSchema[0]);
            }

            @SneakyThrows
            @Override
            public XmlValidator createValidator() {
                return XmlValidatorFactory.createValidator(getXmlSchemas(), "http://www.w3.org/2001/XMLSchema");

            }
        };
        this.setXsdSchemaCollection(schemaCollection);
    }

    public Resource[] getXmlSchemas() {
        List<Resource> schemaResources = new ArrayList<>();
        for (EndpointMetaData metaData : endpointMetaDataList) {
            schemaResources.addAll(Arrays.asList(metaData.getResources()));
        }

        return schemaResources.toArray(new Resource[0]);
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint)
        throws IOException, SAXException, TransformerException {
        String request = messageContext.getRequest().toString();

        if (request.endsWith("addAudio") && !validateAddCase) {
            return true;
        }
        log.info("Validating request start: {}", request);
        boolean res = super.handleRequest(messageContext, endpoint);
        log.info("Validating request end: {}", request);
        return res;
    }
}

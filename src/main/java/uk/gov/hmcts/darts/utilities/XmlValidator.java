package uk.gov.hmcts.darts.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;
import uk.gov.hmcts.darts.ws.CodeAndMessage;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

@Service
@Slf4j
public class XmlValidator {

    public void validate(String xmlDocument, String schemaFilePath) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Validator validator = schemaFactory
                .newSchema(new File(schemaFilePath))
                .newValidator();
            validator.validate(new StreamSource(new StringReader(xmlDocument)));
        } catch (IOException e) {
            log.error("There was a problem reading the schema file: {}", schemaFilePath);
            throw new DartsValidationException(e, CodeAndMessage.INVALID_XML);
        } catch (SAXException e) {
            log.error("Validation failed", e);
            throw new DartsValidationException(e, CodeAndMessage.INVALID_XML);
        }
    }
}

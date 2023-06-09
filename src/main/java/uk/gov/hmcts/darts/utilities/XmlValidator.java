package uk.gov.hmcts.darts.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import uk.gov.hmcts.darts.exceptions.DartsValidationException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

@Service
@Slf4j
public class XmlValidator {

    public void validate(String xmlDocument, String schemaFilePath) {
        try {
            var schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            var validator = schemaFactory
                .newSchema(new File(schemaFilePath))
                .newValidator();
            validator.validate(new StreamSource(new StringReader(xmlDocument)));
        } catch (IOException e) {
            log.error("There was a problem reading the schema file: {}", schemaFilePath);
            throw new DartsValidationException(e);
        } catch (SAXException e) {
            log.error("Validation failed", e);
            throw new DartsValidationException(e);
        }
    }
}

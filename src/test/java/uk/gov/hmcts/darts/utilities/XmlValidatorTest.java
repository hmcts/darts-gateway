package uk.gov.hmcts.darts.utilities;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;
import uk.gov.hmcts.darts.common.exceptions.DartsValidationException;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XmlValidatorTest {

    private static final String INVALID_EVENT_XML = """
        <a:invalidElement xmlns:a="someNS">
            <a:y>some-string</a:y>
            <a:z>000</a:z>
        </a:invalidElement>""";

    private static final String VALID_EVENT_XML = """
        <a:x xmlns:a="someNS">
            <a:y>some-string</a:y>
            <a:z>000</a:z>
        </a:x>""";

    private final XmlValidator xmlValidator = new XmlValidator();

    @Test
    void throwsWhenXmlInvalid() {
        assertThatThrownBy(() -> xmlValidator.validate(INVALID_EVENT_XML, loadSchemaFilePath()))
            .isInstanceOf(DartsValidationException.class)
            .hasCauseInstanceOf(SAXParseException.class);
    }

    @Test
    void doesntThrowWhenXmlValid() {
        assertThatNoException().isThrownBy(() -> xmlValidator.validate(VALID_EVENT_XML, loadSchemaFilePath()));
    }

    private String loadSchemaFilePath() {
        var file = new File(getClass().getClassLoader().getResource("simple-test-schema.xsd").getFile());

        return file.getAbsolutePath();
    }
}

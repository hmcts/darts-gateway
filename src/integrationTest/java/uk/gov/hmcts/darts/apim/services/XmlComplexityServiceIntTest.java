package uk.gov.hmcts.darts.apim.services;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.darts.Application;
import uk.gov.hmcts.darts.apim.config.XmlComplexityConfiguration;
import uk.gov.hmcts.darts.apim.validate.domain.ValidateResult;
import uk.gov.hmcts.darts.apim.validate.exception.XmlConversionException;
import uk.gov.hmcts.darts.apim.validate.services.XmlComplexityService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {Application.class, XmlComplexityConfiguration.class})
@ActiveProfiles("int-test")
class XmlComplexityServiceIntTest {

    @Autowired
    private XmlComplexityService xmlComplexityService;

    @Test
    void testXmlConversionException() {
        XmlConversionException exception;
        exception = assertThrows(XmlConversionException.class,
                                 () -> xmlComplexityService.validateContent("Not XML"),
                                 "XmlConversionException should be thrown");
        assertEquals("Error converting text to XML",
                     exception.getMessage(),
                     "XmlConversionException does not have expected error message");
    }

    @Nested
    class ContentValidTest {

        private void assertValid(String content) {
            ValidateResult result = xmlComplexityService.validateContent(content);
            assertTrue(result.isValid(), "Content should pass XML complexity check");
        }

        @Test
        void testMaxNodesValid() {
            String content = """
            <root>
                <child1>
                    <child2/>
                    <child3/>
                    <child4/>
                </child1>
                <child5>
                    <child6/>
                    <child7/>
                    <child8/>
                </child5>
                <child9/>
            </root>""";

            assertValid(content);
        }

        @Test
        void testMaxLevelsDescendantNodesValid() {
            String content = """
            <root>
                <child1>
                    <child2/>
                </child1>
            </root>""";

            assertValid(content);
        }

        @Test
        void testMaxChildNodesPerNodeValid() {
            String content = """
            <root>
                <child1>
                    <child2/>
                    <child3/>
                    <child4/>
                </child1>
            </root>""";

            assertValid(content);
        }

        @Test
        void testMaxAttributesPerNodeValid() {
            String content = """
            <root>
                <child1>
                    <child2 attr1='1' attr2='2' attr3='3' attr4='4'/>
                </child1>
            </root>""";

            assertValid(content);
        }
    }

    @Nested
    class ContentInvalidTest {

        private void assertInvalid(String content) {
            ValidateResult result = xmlComplexityService.validateContent(content);
            assertFalse(result.isValid(), "Content should fail XML complexity check");
        }

        @Test
        void testMaxNodesInvalid() {
            String content = """
            <root>
                <child1>
                    <child2/>
                    <child3/>
                    <child4/>
                </child1>
                <child5>
                    <child6/>
                    <child7/>
                    <child8/>
                </child5>
                <child9>
                    <child10/>
                </child9>
            </root>""";

            assertInvalid(content);
        }

        @Test
        void testMaxLevelsDescendantNodesInvalid() {
            String content = """
            <root>
                <child1>
                    <child2>
                        <child3/>
                    </child2>
                </child1>
            </root>""";

            assertInvalid(content);
        }

        @Test
        void testMaxChildNodesPerNodeInvalid() {
            String content = """
            <root>
                <child1>
                    <child2/>
                    <child3/>
                    <child4/>
                    <child5/>
                </child1>
            </root>""";

            assertInvalid(content);
        }

        @Test
        void testMaxAttributesPerNodeInvalid() {
            String content = """
            <root>
                <child1>
                    <child2 attr1='1' attr2='2' attr3='3' attr4='4' attr5='5'/>
                </child1>
            </root>""";

            assertInvalid(content);
        }
    }
}

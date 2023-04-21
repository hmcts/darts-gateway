package uk.gov.hmcts.darts.apim.validate.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.darts.apim.validate.exception.XmlComplexityRuleException;
import uk.gov.hmcts.darts.apim.validate.model.XmlComplexityRule;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;
import static javax.xml.xpath.XPathConstants.BOOLEAN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class XmlComplexityUtilTest {

    private XmlComplexityRule xmlComplexityRule;

    /** Class being tested. */
    private XmlComplexityUtil xmlComplexityUtil;

    @BeforeEach
    void setUp() {
        xmlComplexityRule = new XmlComplexityRule("maxNodes", "count(//*) > 2");

        xmlComplexityUtil = new XmlComplexityUtil();
    }

    @Test
    void testComplexityRuleBroken() throws ParserConfigurationException, SAXException, IOException {
        Document testDoc = convertTextToXml("<root><child1/><child2/></root>");

        boolean isRuleBroken = xmlComplexityUtil.isComplexityRuleBroken(testDoc, xmlComplexityRule);
        assertTrue(isRuleBroken, "XmlComplexityRule should be broken");
    }

    @Test
    void testComplexityRuleNotBroken() throws ParserConfigurationException, SAXException, IOException {
        Document testDoc = convertTextToXml("<root><child1/></root>");

        boolean isRuleBroken = xmlComplexityUtil.isComplexityRuleBroken(testDoc, xmlComplexityRule);
        assertFalse(isRuleBroken, "XmlComplexityRule should not be broken");
    }

    @Test
    void testXmlComplexityRuleException() throws XPathExpressionException {
        Document mockDocument = mock(Document.class);
        String ruleXpath = xmlComplexityRule.getXpath();

        try (MockedStatic<XPathFactory> mockStaticXPathFactory = mockStatic(XPathFactory.class)) {
            XPathFactory mockXPathFactory = mock(XPathFactory.class);
            XPath mockXPath = mock(XPath.class);

            when(mockXPath.evaluate(ruleXpath, mockDocument, BOOLEAN)).thenThrow(XPathExpressionException.class);
            when(mockXPathFactory.newXPath()).thenReturn(mockXPath);
            mockStaticXPathFactory.when(XPathFactory::newDefaultInstance).thenReturn(mockXPathFactory);

            assertThrows(XmlComplexityRuleException.class,
                         () -> xmlComplexityUtil.isComplexityRuleBroken(mockDocument, xmlComplexityRule),
                         "XmlComplexityRuleException should be thrown");

            mockStaticXPathFactory.verify(XPathFactory::newDefaultInstance);
            verify(mockXPathFactory).newXPath();
            verify(mockXPath).evaluate(ruleXpath, mockDocument, BOOLEAN);
        }
    }

    private Document convertTextToXml(String text) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
        documentBuilderFactory.setFeature(FEATURE_SECURE_PROCESSING, true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(text)));
    }
}

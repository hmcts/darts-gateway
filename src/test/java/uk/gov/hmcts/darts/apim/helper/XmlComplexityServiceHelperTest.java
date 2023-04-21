package uk.gov.hmcts.darts.apim.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.darts.apim.config.XmlComplexityConfiguration;
import uk.gov.hmcts.darts.apim.validate.exception.XmlConfigurationException;
import uk.gov.hmcts.darts.apim.validate.exception.XmlConversionException;
import uk.gov.hmcts.darts.apim.validate.helper.XmlComplexityServiceHelper;
import uk.gov.hmcts.darts.apim.validate.model.XmlComplexityRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class XmlComplexityServiceHelperTest {

    private static final int MAX_NODES = 1;
    private static final int MAX_LEVELS_DESCENDANT_NODES = 2;
    private static final int MAX_CHILD_NODES_PER_NODE = 3;
    private static final int MAX_ATTRIBUTES_PER_NODE = 4;

    private static final String XPATH_MAX_NODES = "count(//*) > %d";
    private static final String XPATH_MAX_LEVELS_DESCENDANT_NODES = "count(//*[count(ancestor::*) > %d]) > 0";
    private static final String XPATH_MAX_CHILD_NODES_PER_NODE = "count(//*[count(./*) > %d]) > 0";
    private static final String XPATH_MAX_ATTRIBUTES_PER_NODE = "count(//*[count(@*) > %d]) > 0";

    /** Class being tested. */
    private XmlComplexityServiceHelper xmlComplexityServiceHelper;

    @BeforeEach
    void setUp() {
        xmlComplexityServiceHelper = new XmlComplexityServiceHelper();
    }

    private void assertXmlComplexityRule(String expectedRuleName,
                                         String expectedRuleXpath,
                                         List<XmlComplexityRule> actualRules) {
        XmlComplexityRule actualRule = actualRules.stream()
            .filter(rule -> expectedRuleName.equals(rule.getName()))
            .findAny()
            .orElse(null);

        if (actualRule == null) {
            fail("Expected rule '" + expectedRuleName + "' not found");
        } else {
            assertEquals(expectedRuleXpath,
                         actualRule.getXpath(),
                         "XPath of rule '" + actualRule.getName() + "' does not match expected value");
        }
    }

    @Test
    void testGetComplexityRules() {
        ArrayList<XmlComplexityRule> expectedRules = new ArrayList<>();

        String xpathMaxNodes =
            String.format(XPATH_MAX_NODES, MAX_NODES);
        String xpathMaxLevelsDescendantNodes =
            String.format(XPATH_MAX_LEVELS_DESCENDANT_NODES, MAX_LEVELS_DESCENDANT_NODES);
        String xpathMaxChildNodesPerNode =
            String.format(XPATH_MAX_CHILD_NODES_PER_NODE, MAX_CHILD_NODES_PER_NODE);
        String xpathMaxAttributesPerNode =
            String.format(XPATH_MAX_ATTRIBUTES_PER_NODE, MAX_ATTRIBUTES_PER_NODE);

        expectedRules.add(new XmlComplexityRule("maxNodes", xpathMaxNodes));
        expectedRules.add(new XmlComplexityRule("maxLevelsDescendantNodes", xpathMaxLevelsDescendantNodes));
        expectedRules.add(new XmlComplexityRule("maxChildNodesPerNode", xpathMaxChildNodesPerNode));
        expectedRules.add(new XmlComplexityRule("maxAttributesPerNode", xpathMaxAttributesPerNode));

        XmlComplexityConfiguration config = new XmlComplexityConfiguration();
        config.setMaxNodes(MAX_NODES);
        config.setMaxLevelsDescendantNodes(MAX_LEVELS_DESCENDANT_NODES);
        config.setMaxChildNodesPerNode(MAX_CHILD_NODES_PER_NODE);
        config.setMaxAttributesPerNode(MAX_ATTRIBUTES_PER_NODE);

        List<XmlComplexityRule> actualRules = xmlComplexityServiceHelper.getXmlComplexityRules(config);

        assertEquals(4, actualRules.size(), "Unexpected number of rules returned");

        for (XmlComplexityRule expectedRule : expectedRules) {
            assertXmlComplexityRule(expectedRule.getName(), expectedRule.getXpath(), actualRules);
        }
    }

    @Test
    void testConvertTextToXml() {
        Document doc1 = xmlComplexityServiceHelper.convertTextToXml("<root><child1/><child2/></root>");

        NodeList doc1RootNodes = doc1.getElementsByTagName("root");
        assertEquals(1, doc1RootNodes.getLength(), "Document1 should have root node");

        Node doc1rootNode = doc1RootNodes.item(0);
        NodeList doc1ChildNodes = doc1rootNode.getChildNodes();
        assertEquals(2, doc1ChildNodes.getLength(), "Document1 root node should have 2 child nodes");

        assertEquals("child1",
                     doc1ChildNodes.item(0).getNodeName(),
                     "Document1 root first child node should be 'child1'");
        assertEquals("child2",
                     doc1ChildNodes.item(1).getNodeName(),
                     "Document1 root second child node should be 'child2'");

        // Now convert a second set of text to cover scenario where document builder has already been initialised
        Document doc2 = xmlComplexityServiceHelper.convertTextToXml("<root><child1/></root>");

        NodeList doc2RootNodes = doc2.getElementsByTagName("root");
        assertEquals(1, doc2RootNodes.getLength(), "Document2 should have root node");

        Node doc2rootNode = doc2RootNodes.item(0);
        NodeList doc2ChildNodes = doc2rootNode.getChildNodes();
        assertEquals(1, doc2ChildNodes.getLength(), "Document2 root node should have 1 child node");

        assertEquals("child1",
                     doc2ChildNodes.item(0).getNodeName(),
                     "Document2 root first child node should be 'child1'");
    }

    @Test
    void testXmlConfigurationException() throws ParserConfigurationException {
        try (
            MockedStatic<DocumentBuilderFactory> mockStaticDocumentBuilderFactory =
                mockStatic(DocumentBuilderFactory.class)
        ) {
            DocumentBuilderFactory mockDocumentBuilderFactory = mock(DocumentBuilderFactory.class);

            mockStaticDocumentBuilderFactory.when(DocumentBuilderFactory::newDefaultInstance)
                .thenReturn(mockDocumentBuilderFactory);
            // Mimic a ParserConfigurationException thrown by setFeature() - doesn't matter what causes it
            doThrow(ParserConfigurationException.class).when(mockDocumentBuilderFactory)
                .setFeature(anyString(), anyBoolean());

            assertThrows(XmlConfigurationException.class,
                         () -> xmlComplexityServiceHelper.convertTextToXml("<root/>"),
                         "XmlConfigurationException should be thrown");

            verify(mockDocumentBuilderFactory).setFeature(anyString(), anyBoolean());
            mockStaticDocumentBuilderFactory.verify(DocumentBuilderFactory::newDefaultInstance);
        }
    }

    @Test
    void testXmlConversionException() throws ParserConfigurationException, SAXException, IOException {
        try (
            MockedStatic<DocumentBuilderFactory> mockStaticDocumentBuilderFactory =
                mockStatic(DocumentBuilderFactory.class)
        ) {
            DocumentBuilderFactory mockDocumentBuilderFactory = mock(DocumentBuilderFactory.class);
            DocumentBuilder mockDocumentBuilder = mock(DocumentBuilder.class);

            mockStaticDocumentBuilderFactory.when(DocumentBuilderFactory::newDefaultInstance)
                .thenReturn(mockDocumentBuilderFactory);
            when(mockDocumentBuilderFactory.newDocumentBuilder()).thenReturn(mockDocumentBuilder);

            when(mockDocumentBuilder.parse(any(InputSource.class))).thenThrow(SAXException.class);

            assertThrows(XmlConversionException.class,
                         () -> xmlComplexityServiceHelper.convertTextToXml("This is not an XML string!"),
                         "XmlConversionException should be thrown");

            verify(mockDocumentBuilder).parse(any(InputSource.class));
            verify(mockDocumentBuilderFactory).newDocumentBuilder();
            mockStaticDocumentBuilderFactory.verify(DocumentBuilderFactory::newDefaultInstance);
        }
    }
}

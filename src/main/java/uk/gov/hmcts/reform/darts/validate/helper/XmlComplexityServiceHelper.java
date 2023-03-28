package uk.gov.hmcts.reform.darts.validate.helper;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.hmcts.reform.darts.config.XmlComplexityConfiguration;
import uk.gov.hmcts.reform.darts.validate.exception.XmlConfigurationException;
import uk.gov.hmcts.reform.darts.validate.exception.XmlConversionException;
import uk.gov.hmcts.reform.darts.validate.model.XmlComplexityRule;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;

@Component
public class XmlComplexityServiceHelper {

    private static final String XPATH_MAX_NODES = "count(//*) > %d";
    private static final String XPATH_MAX_LEVEL_DESCENDANT_NODES = "count(//*[count(ancestor::*) > %d]) > 0";
    private static final String XPATH_MAX_CHILD_NODES_PER_NODE = "count(//*[count(./*) > %d]) > 0";
    private static final String XPATH_MAX_ATTRIBUTES_PER_NODE = "count(//*[count(@*) > %d]) > 0";

    private DocumentBuilder documentBuilder;

    public List<XmlComplexityRule> getXmlComplexityRules(XmlComplexityConfiguration config) {
        ArrayList<XmlComplexityRule> rules = new ArrayList<>();

        rules.add(createRule("maxNodes", XPATH_MAX_NODES, config.getMaxNodes()));
        rules.add(createRule("maxLevelsDescendantNodes",
                             XPATH_MAX_LEVEL_DESCENDANT_NODES,
                             config.getMaxLevelsDescendantNodes()));
        rules.add(createRule("maxChildNodesPerNode", XPATH_MAX_CHILD_NODES_PER_NODE, config.getMaxChildNodesPerNode()));
        rules.add(createRule("maxAttributesPerNode", XPATH_MAX_ATTRIBUTES_PER_NODE, config.getMaxAttributesPerNode()));

        return rules;
    }

    public Document convertTextToXml(String text) {
        if (documentBuilder == null) {
            createDocumentBuilder();
        }

        try {
            return documentBuilder.parse(new InputSource(new StringReader(text)));
        } catch (SAXException | IOException e) {
            throw new XmlConversionException("Error converting text to XML", e);
        }
    }

    private XmlComplexityRule createRule(String name, String xpath, int limit) {
        return new XmlComplexityRule(name, String.format(xpath, limit));
    }

    private void createDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();

        try {
            documentBuilderFactory.setFeature(FEATURE_SECURE_PROCESSING, true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlConfigurationException("Error creating document builder", e);
        }
    }
}

package uk.gov.hmcts.darts.utilities;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;


class NodeUtilTest {

    @Test
    void findNodeNotFound() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        String myDocumentStr = "<shop><shoes>brand1</shoes><tshirt>brand2</tshirt><socks>brand3</socks></shop>";
        Node myDocument = getNode(myDocumentStr);
        XPathExpression path = XPathFactory.newInstance().newXPath().compile("/shop/*");

        NodeList nodeList = (NodeList) path.evaluate(myDocument, XPathConstants.NODESET);


        Optional<Node> response = NodeUtil.findNode("hello", nodeList);
        assertTrue(response.isEmpty());
    }

    private static Node getNode(String myDocumentStr) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        Node myDocument = builder.parse(new ByteArrayInputStream(myDocumentStr.getBytes()));
        return myDocument;
    }
}

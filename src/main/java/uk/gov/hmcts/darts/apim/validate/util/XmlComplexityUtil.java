package uk.gov.hmcts.darts.apim.validate.util;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import uk.gov.hmcts.darts.apim.validate.exception.XmlComplexityRuleException;
import uk.gov.hmcts.darts.apim.validate.model.XmlComplexityRule;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static javax.xml.xpath.XPathConstants.BOOLEAN;

@Component
public class XmlComplexityUtil {

    public boolean isComplexityRuleBroken(Document doc, XmlComplexityRule xmlComplexityRule) {
        XPath xpath = XPathFactory.newDefaultInstance().newXPath();

        try {
            return (Boolean) xpath.evaluate(xmlComplexityRule.getXpath(), doc, BOOLEAN);
        } catch (XPathExpressionException e) {
            throw new XmlComplexityRuleException(
                "Complexity rule xpath evaluation failed: " + xmlComplexityRule.getName(),
                e
            );
        }
    }
}

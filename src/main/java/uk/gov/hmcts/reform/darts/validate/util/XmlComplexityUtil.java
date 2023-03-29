package uk.gov.hmcts.reform.darts.validate.util;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import uk.gov.hmcts.reform.darts.validate.exception.XmlComplexityRuleException;
import uk.gov.hmcts.reform.darts.validate.model.XmlComplexityRule;

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
